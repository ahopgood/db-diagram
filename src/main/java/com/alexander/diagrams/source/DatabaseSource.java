package com.alexander.diagrams.source;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "SQL_INJECTION_JDBC",
    "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING"},
    justification = "https://github.com/spotbugs/spotbugs/issues/259, Table name is obtained from the DB itself not from user input")
public class DatabaseSource implements Source {

    private static final int SHOW_TABLES_COLUMN_INDEX = 1;
    private static final int DESCRIBE_TABLE_COLUMN_INDEX = 2;
    private Iterator<String> tableNamesIterator;

    private final String password;
    private final String username;
    private final String databaseUrl;
    private final String databaseName;

    private static final String CONNECTION_STRING = "jdbc:mysql://%s/%s?useTimezone=true&serverTimezone=UTC";

    /**
     * Creates a database source to read database create table statements.
     * @param password the password for the account to use
     * @param username the username for the account to use
     * @param databaseName the name of the database schema to use
     * @param databaseUrl the host (and optional port) of the database server e.g. localhost or 127.0.0.1:3306.
     * @param tableNames (optional) a {@link List} of Strings representing a specific set of table names to use as a
     *                   source instead of using the init() method to pull all possible table names from the database.
     */
    @Builder
    public DatabaseSource(String password, String username, String databaseName, String databaseUrl,
                          List<String> tableNames) {
        this.password = password;
        this.username = username;
        this.databaseName = databaseName;
        this.databaseUrl = databaseUrl;
        if (tableNames != null) {
            this.tableNamesIterator = tableNames.iterator();
        }
    }

    private void init() {
        try {
            Connection conn = getConnection();
            try (PreparedStatement listTablesStatement = conn.prepareStatement("SHOW TABLES;")) {
                try (ResultSet listTablesResult = listTablesStatement.executeQuery()) {
                    List<String> tableNames = new LinkedList<>();
                    while (listTablesResult.next()) {
                        tableNames.add(listTablesResult.getString(SHOW_TABLES_COLUMN_INDEX));
                    }
                    tableNamesIterator = tableNames.iterator();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("There was an issue listing the tables in the database", e);
        }
    }

    @Override
    public boolean hasNext() {
        if (tableNamesIterator == null) {
            init();
        }
        return tableNamesIterator.hasNext();
    }

    @Override
    public List<String> next() {
        String tableName = tableNamesIterator.next();
        return getDescribeTable(tableName);
    }

    protected List<String> getDescribeTable(String tableName) {
        String query = String.format("SHOW CREATE TABLE %s;", tableName);
        try {
            Connection conn = getConnection();
            try {
                PreparedStatement showCreateTableStatement = conn.prepareStatement(query);
                try {
                    ResultSet createTableResult = showCreateTableStatement.executeQuery();
                    try {
                        if (createTableResult.next()) {
                            String describeBlock = createTableResult.getString(DESCRIBE_TABLE_COLUMN_INDEX);
                            return describeBlock.lines().collect(Collectors.toList());
                        }
                    } finally {
                        createTableResult.close();
                    }
                } finally {
                    showCreateTableStatement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to describe table " + tableName, e);
        }
        return new LinkedList<>();
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager
            .getConnection(String.format(CONNECTION_STRING, databaseUrl, databaseName), username, password);
    }
}

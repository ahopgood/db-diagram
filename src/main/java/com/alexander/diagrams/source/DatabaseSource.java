package com.alexander.diagrams.source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

public class DatabaseSource implements Source {

    private static final int SHOW_TABLES_COLUMN_INDEX = 1;
    private static final int DESCRIBE_TABLE_COLUMN_INDEX = 2;
    private Iterator<String> tableNamesIterator;

    private final String password;
    private final String username;
    private final String databaseUrl;
    private final String databaseName;

    private static final String CONNECTION_STRING = "jdbc:mysql://%s/%s?useTimezone=true&serverTimezone=UTC";

    @Builder
    public DatabaseSource(String password, String username, String databaseName, String databaseUrl) {
        this.password = password;
        this.username = username;
        this.databaseName = databaseName;
        this.databaseUrl = databaseUrl;
        init();
    }

    private void init() {
        try {
            Connection conn = DriverManager
                .getConnection(String.format(CONNECTION_STRING, databaseUrl, databaseName), username, password);
            try {
                ResultSet listTablesResult = conn.prepareStatement("SHOW TABLES;").executeQuery();
                List<String> tableNames = new LinkedList<>();
                while (listTablesResult.next()) {
                    tableNames.add(listTablesResult.getString(SHOW_TABLES_COLUMN_INDEX));
                }
                tableNamesIterator = tableNames.iterator();
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("There was an issue listing the tables in the database", e);
        }
    }

    @Override
    public boolean hasNext() {
        return tableNamesIterator.hasNext();
    }

    @Override
    public List<String> next() {
        String tableName = tableNamesIterator.next();
        return getDescribeTable(tableName);
    }

    protected List<String> getDescribeTable(String tableName) {
        try {
            Connection conn = DriverManager.getConnection(String.format(CONNECTION_STRING, databaseUrl, databaseName), username, password);
            try {
                ResultSet createTableResult = conn.prepareStatement(String.format("SHOW CREATE TABLE %s;", tableName)).executeQuery();
                if (createTableResult.next()) {
                    String describeBlock = createTableResult.getString(DESCRIBE_TABLE_COLUMN_INDEX);
                    return describeBlock.lines().collect(Collectors.toList());
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to describe table " + tableName, e);
        }
        return new LinkedList<>();
    }
}

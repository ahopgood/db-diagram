package com.alexander.diagrams.source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.management.RuntimeMBeanException;

public class DatabaseSource implements Source {

    protected static final int SHOW_TABLES_COLUMN_INDEX = 1;
    private Iterator<String> tableNamesIterator;

    public DatabaseSource() {
        init();
    }

    private void init() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?useTimezone=true&serverTimezone=UTC", "root", "");
            ResultSet listTablesResult = conn.prepareStatement("SHOW TABLES;").executeQuery();
            List<String> tableNames = new LinkedList<>();
            while (listTablesResult.next()) {
                tableNames.add(listTablesResult.getString(SHOW_TABLES_COLUMN_INDEX));
            }
            tableNamesIterator = tableNames.iterator();
        } catch (SQLException e) {
            throw new RuntimeException("There was an issue list the tables in the database", e);
        }
    }

    @Override
    public boolean hasNext() {
        return tableNamesIterator.hasNext();
    }

    @Override
    public List<String> next() {
        String tableName = tableNamesIterator.next();
        return null;
    }

    protected List<String> getDescribeTable(String tableName) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?useTimezone=true&serverTimezone=UTC", "root", "");
            ResultSet createTableResult = conn.prepareStatement(String.format("SHOW CREATE TABLE %s;", tableName)).executeQuery();
            if (createTableResult.next()) {
                String describeBlock = createTableResult.getString(2);
                return describeBlock.lines().collect(Collectors.toList());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to describe table " + tableName, e);
        }
        return new LinkedList<>();
    }
}

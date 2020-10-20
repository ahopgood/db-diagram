package com.alexander.diagrams.source;

import java.sql.Connection;
import lombok.Builder;

public class MockConnectionDatabaseSource extends DatabaseSource {
    private final Connection connection;
    /**
     * Creates a database source to read database create table statements.
     *
     * @param password     the password for the account to use
     * @param username     the username for the account to use
     * @param databaseName the name of the database schema to use
     * @param databaseUrl  the host (and optional port) of the database server e.g. localhost or 127.0.0.1:3306.
     */
    @Builder(builderMethodName = "subbuilder")
    public MockConnectionDatabaseSource(String password, String username, String databaseName, String databaseUrl,
                                        Connection connection) {
        super(password, username, databaseName, databaseUrl, null);
        this.connection = connection;
    }

    @Override
    protected Connection getConnection() {
        return connection;
    }
}

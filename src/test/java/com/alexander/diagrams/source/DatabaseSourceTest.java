package com.alexander.diagrams.source;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseSourceTest {

    private static final String rootUsername = "root";
    private static final String rootPassword = "";
    private static final String sourceUsername = "source";
    private static final String sourcePassword = "sourcePassword";
    private static final String database = "mydatabase";
    private static final String databaseUrl = "localhost";

    private String mocktable = "mockitymock";

    private static DB db;
    private static Connection conn;

    @BeforeAll
    static void setup() throws ManagedProcessException, SQLException {
        DBConfiguration configuration = DBConfigurationBuilder.newBuilder()
            .setPort(3306)
            .setSecurityDisabled(false)
            .addArg("--user=root")
            .build();
        db = DB.newEmbeddedDB(configuration);
        db.start();
        db.createDB(database, "root", "");
        conn = DriverManager
            .getConnection("jdbc:mysql://" + databaseUrl + "/" + database + "?useTimezone=true&serverTimezone=UTC",
                rootUsername,
                rootPassword);

        PreparedStatement user = conn.prepareStatement(createUser());
        user.execute();
        user.closeOnCompletion();

        PreparedStatement grantPrivileges = conn.prepareStatement(grantPrivileges());
        grantPrivileges.execute();
        grantPrivileges.closeOnCompletion();

        PreparedStatement attributeTypes = conn.prepareStatement(createAttributeTypesTable());
        attributeTypes.execute();
        attributeTypes.closeOnCompletion();

        PreparedStatement attributes = conn.prepareStatement(createAttributesTable());
        attributes.execute();
        attributes.closeOnCompletion();
    }

    @AfterAll
    static void teardown() throws ManagedProcessException, SQLException {
        conn.close();
        db.stop();
    }

    @Test
    void testHasNext() {
        DatabaseSource source = DatabaseSource.builder()
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();
        assertThat(source.hasNext()).isTrue();
        assertSourceSize(2, source);
    }

    @Test
    void testHasNext_givenTableNames() {
        DatabaseSource source = DatabaseSource.builder()
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .tableNames(List.of("attribute_types"))
            .build();
        assertThat(source.hasNext()).isTrue();
        assertSourceSize(1, source);
    }

    @Test
    void testNext() {
        DatabaseSource source = DatabaseSource.builder()
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();
        assertThat(source.hasNext()).isTrue();
        assertThat(source.next()).hasSize(8);
        assertThat(source.next()).hasSize(15);

    }

    @Test
    void testWhenWrongPassword_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () ->  DatabaseSource.builder()
                .username(sourceUsername)
                .password("nothepassword")
                .databaseName(database)
                .databaseUrl(databaseUrl)
                .build()
                .hasNext());
    }

    @Test
    void testWhenWrongUsername_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username("nottheusername")
                .password(sourcePassword)
                .databaseName(database)
                .databaseUrl(databaseUrl)
                .build()
                .hasNext());
    }

    @Test
    void testWhenWrongUrl_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username(sourceUsername)
                .password(sourcePassword)
                .databaseName(database)
                .databaseUrl("999.999.999.999")
                .build()
                .hasNext());
    }

    @Test
    void testWhenWrongDatabase_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username(sourceUsername)
                .password(sourcePassword)
                .databaseName("nothedatabase")
                .databaseUrl(databaseUrl)
                .build()
                .hasNext());
    }

    @Test
    void testWhenTableDoesntExist() {
        DatabaseSource source = DatabaseSource.builder()
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();
        assertThrows(RuntimeException.class,
            () -> source.getDescribeTable("unknownTable"));
    }

    // Implement subclass with a Mock Connection
    @Test
    void testGetDescribeTable_whenConnection_thenThrowSqlException() throws SQLException {
        Connection conn = mock(Connection.class);
        doNothing().when(conn).close();

        when(conn.prepareStatement("SHOW CREATE TABLE " + mocktable + ";")).thenThrow(SQLException.class);

        DatabaseSource source = MockConnectionDatabaseSource.subbuilder()
            .connection(conn)
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();

        assertThrows(RuntimeException.class, () -> source.getDescribeTable(mocktable));
        verify(conn, times(1)).close();
    }

    @Test
    void testGetDescribeTable_whenConnectionClose_thenThrowSqlException() throws SQLException {
        Connection conn = mock(Connection.class);
        doThrow(SQLException.class).when(conn).close();

        when(conn.prepareStatement("SHOW CREATE TABLE " + mocktable + ";")).thenThrow(SQLException.class);

        DatabaseSource source = MockConnectionDatabaseSource.subbuilder()
            .connection(conn)
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();

        assertThrows(RuntimeException.class, () -> source.getDescribeTable(mocktable));
        verify(conn, times(1)).close();
    }

    @Test
    void testGetDescribeTable_whenPrepareStatement_thenThrowSqlException() throws SQLException {
        PreparedStatement showCreateTableStatement = mock(PreparedStatement.class);
        when(showCreateTableStatement.executeQuery()).thenThrow(SQLException.class);

        Connection conn = mock(Connection.class);
        doNothing().when(conn).close();

        when(conn.prepareStatement("SHOW CREATE TABLE " + mocktable + ";")).thenReturn(showCreateTableStatement);

        DatabaseSource source = MockConnectionDatabaseSource.subbuilder()
            .connection(conn)
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();

        assertThrows(RuntimeException.class, () -> source.getDescribeTable(mocktable));
        verify(conn, times(1)).close();
    }

    @Test
    void testGetDescribeTable_whenExecuteQuery_thenThrowSqlException() throws SQLException {
        //Set up init() mocking
//        ResultSet listTablesResult = mock(ResultSet.class);
//        when(listTablesResult.next()).thenReturn(true);
//        when(listTablesResult.getString(1)).thenReturn(mocktable);
//
//        PreparedStatement listTablesStatement = mock(PreparedStatement.class);
//        when(listTablesStatement.executeQuery()).thenReturn(listTablesResult);

        //Set up getDescribeTable call.
        PreparedStatement showCreateTableStatement = mock(PreparedStatement.class);
        when(showCreateTableStatement.executeQuery()).thenThrow(SQLException.class);

        Connection conn = mock(Connection.class);
        doNothing().when(conn).close();

        when(conn.prepareStatement("SHOW CREATE TABLE " + mocktable + ";")).thenReturn(showCreateTableStatement);
//        when(conn.prepareStatement("SHOW TABLES;")).thenReturn(listTablesStatement);

        DatabaseSource source = MockConnectionDatabaseSource.subbuilder()
            .connection(conn)
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();

        assertThrows(RuntimeException.class, () -> source.getDescribeTable(mocktable));
        verify(conn, times(1)).close();
    }

    @Test
    void testGetDescribeTable_givenEmptyResultSet() throws SQLException {
        //Set up init() mocking
//        ResultSet listTablesResult = mock(ResultSet.class);
//        when(listTablesResult.next()).thenReturn(true);
//        when(listTablesResult.getString(1)).thenReturn(mocktable);
//
//        PreparedStatement listTablesStatement = mock(PreparedStatement.class);
//        when(listTablesStatement.executeQuery()).thenReturn(listTablesResult);

        //Set up getDescribeTable call.
        ResultSet createTableResult = mock(ResultSet.class);
        when(createTableResult.next()).thenReturn(false);

        PreparedStatement showCreateTableStatement = mock(PreparedStatement.class);
        when(showCreateTableStatement.executeQuery()).thenReturn(createTableResult);

        Connection conn = mock(Connection.class);
        doNothing().when(conn).close();

        when(conn.prepareStatement("SHOW CREATE TABLE " + mocktable + ";")).thenReturn(showCreateTableStatement);
//        when(conn.prepareStatement("SHOW TABLES;")).thenReturn(listTablesStatement);

        DatabaseSource source = MockConnectionDatabaseSource.subbuilder()
            .connection(conn)
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();

        assertThat(source.getDescribeTable(mocktable)).isEqualTo(new LinkedList<>());
        verify(conn, times(1)).close();
    }

    private static String createAttributeTypesTable() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE attribute_types (");
        builder.append("attribute_type_id varchar(36) NOT NULL PRIMARY KEY,");
        builder.append("name varchar(255) DEFAULT NULL,");
        builder.append("description varchar(1024) DEFAULT NULL,");
        builder.append("position int(11) NOT NULL DEFAULT '0',");
        builder.append("UNIQUE(attribute_type_id)");
        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC ;");
        return builder.toString();
    }

    private static String createAttributesTable() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE attributes (");
        builder.append("attribute_id varchar(36) NOT NULL PRIMARY KEY,");
        builder.append("name varchar(255) DEFAULT NULL,");
        builder.append("description varchar(1024) DEFAULT NULL,");
        builder.append("attribute_type_id varchar(36) DEFAULT NULL,");
        builder.append("label_partner varchar(255) DEFAULT NULL,");
        builder.append("label_customer varchar(255) DEFAULT NULL,");
        builder.append("is_legacy tinyint(1) NOT NULL DEFAULT '0',");
        builder.append("max_selectable_attribute_values int(11) DEFAULT NULL,");
        builder.append("position int(11) NOT NULL DEFAULT '0',");
        builder.append("is_mandatory tinyint(1) NOT NULL DEFAULT '0',");
        builder.append("UNIQUE(attribute_type_id),");
        builder.append("FOREIGN KEY (attribute_type_id) REFERENCES attribute_types (attribute_type_id)");
        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC ;");
        return builder.toString();
    }

    private static String createUser() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE USER '" + sourceUsername + "'@'localhost' IDENTIFIED BY '" + sourcePassword + "';");
        return builder.toString();
    }

    private static String grantPrivileges() {
        StringBuilder builder = new StringBuilder();
        builder.append("GRANT ALL PRIVILEGES ON " + database + ".* TO '" + sourceUsername + "'@'localhost';");
        return builder.toString();
    }

    private void assertSourceSize(int expectedSize, Source source) {
        int i = 0;
        while (source.hasNext()) {
            source.next();
            i++;
        }
        assertEquals(expectedSize, i);
    }
}

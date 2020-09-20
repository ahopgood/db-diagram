package com.alexander.diagrams.source;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseSourceTest {

    private final String rootUsername = "root";
    private final String rootPassword = "";
    private final String database = "test";
    private final String databaseUrl = "localhost";
    private final String tablename = "attribute_types";

    private static DB db;
    private static Connection conn;

    @BeforeAll
    static void setup() throws ManagedProcessException, SQLException {
        DBConfiguration configuration = DBConfigurationBuilder.newBuilder()
            .setPort(3306)
            .setSecurityDisabled(false)
            .build();
        db = DB.newEmbeddedDB(configuration);
        db.start();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/test?useTimezone=true&serverTimezone=UTC", "root", "");
        conn.prepareStatement(createAttributeTypesTable()).execute();
        conn.prepareStatement(createAttributesTable()).execute();
    }

    @AfterAll
    static void teardown() throws ManagedProcessException, SQLException {
        conn.close();
        db.stop();
    }

    @Test
    void testHasNext() {
        DatabaseSource source = DatabaseSource.builder()
            .username(rootUsername)
            .password(rootPassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();
        assertThat(source.hasNext()).isTrue();
        assertSourceSize(2, source);
    }

    @Test
    void testNext() {
        DatabaseSource source = DatabaseSource.builder()
            .username(rootUsername)
            .password(rootPassword)
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
                .username(rootUsername)
                .password("nothepassword")
                .databaseName(database)
                .databaseUrl(databaseUrl)
                .build());
    }

    @Test
    void testWhenWrongUsername_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username("notheusername")
                .password(rootPassword)
                .databaseName(database)
                .databaseUrl(databaseUrl)
                .build());

    }

    @Test
    void testWhenWrongUrl_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username(rootUsername)
                .password(rootPassword)
                .databaseName(database)
                .databaseUrl("0.0.0.0")
                .build());
    }

    @Test
    void testWhenWrongDatabase_thenAuthenticationFailed() {
        assertThrows(RuntimeException.class,
            () -> DatabaseSource.builder()
                .username(rootUsername)
                .password(rootPassword)
                .databaseName("nothedatabase")
                .databaseUrl(databaseUrl)
                .build());
    }

    @Test
    void testWhenTableDoesntExist() {
        DatabaseSource source = DatabaseSource.builder()
            .username(rootUsername)
            .password(rootPassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .build();
        assertThrows(RuntimeException.class,
            () -> source.getDescribeTable("unknownTable"));
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

    private void assertSourceSize(int expectedSize, Source source) {
        int i = 0;
        while (source.hasNext()) {
            source.next();
            i++;
        }
        assertEquals(expectedSize, i);
    }
}

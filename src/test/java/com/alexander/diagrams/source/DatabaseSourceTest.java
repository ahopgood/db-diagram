package com.alexander.diagrams.source;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseSourceTest {

    private final String rootUsername = "root";
    private final String rootPassword = "";
    private final String database = "test";
    private final String tablename = "attribute_types";

    private DB db;

    @BeforeEach
    void setup() throws ManagedProcessException {
        db = DB.newEmbeddedDB(3306);
    }

    @AfterEach
    void teardown() throws ManagedProcessException {
        db.stop();
    }

    @Test
    void testConnection() throws ManagedProcessException, SQLException {
        db.start();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?useTimezone=true&serverTimezone=UTC", "root", "");
        conn.prepareStatement(createTable()).execute();

        ResultSet result = conn.prepareStatement("SHOW CREATE TABLE attribute_types;").executeQuery();
//        ResultSet result = conn.prepareStatement("SELECT * FROM attribute_types;").executeQuery();
        if (result.next()) {
//            System.out.println(result.getString(1));
            String describeBlock = result.getString(2);
//            System.out.println(describeBlock);

            describeBlock.lines().forEach(
                s -> System.out.println(s)
            );
        }
    }

    @Test
    void hasNext() {
    }

    @Test
    void next() {
    }

    private String createTable() {
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
}

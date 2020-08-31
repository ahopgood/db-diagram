package com.alexander.diagrams.db;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.PrimaryKey;
import com.alexander.diagrams.model.Table;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class MySQLRegexParserTest {

    private MySQLRegexParser parser = new MySQLRegexParser();

    @Test
    void testToTable() {
        String input = "CREATE TABLE `attribute_values` (";
        Table table = parser.toTable(input);
        assertThat(table.getColumns()).isNull();
        assertThat(table.getName()).isEqualTo("attribute_values");
    }

    @Test
    void testToTable_whenNotACreateTableStatement() {
        String input = "  `id` int(11) NOT NULL AUTO_INCREMENT,";
        Table table = parser.toTable(input);
        assertThat(table.getColumns()).isNull();
        assertThat(table.getName()).isEmpty();
    }

    @Test
    void testToColumn_autoIncrement() {
        String input = "  `id` int(11) AUTO_INCREMENT,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("id");
        assertThat(column.getType()).isEqualTo("int");
        assertThat(column.getScale()).isEqualTo("11");
        assertThat(column.isAutoIncrement()).isTrue();
    }

    @Test
    void testToColumn_notNull() {
        String input = "  `id` int(11) NOT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("id");
        assertThat(column.getType()).isEqualTo("int");
        assertThat(column.getScale()).isEqualTo("11");
        assertThat(column.isNotNull()).isTrue();
    }

    @Test
    void testToColumn_defaultValueInteger() {
        String input = "`allows_gift_message` tinyint(1) DEFAULT '0',";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("allows_gift_message");
        assertThat(column.getType()).isEqualTo("tinyint");
        assertThat(column.getScale()).isEqualTo("1");
        assertThat(column.getDefaultValue()).isEqualTo("'0'");
    }

    @Test
    void testToColumn_defaultValueNull() {
        String input = "`specific_commission_rate` decimal(4,2) DEFAULT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("specific_commission_rate");
        assertThat(column.getType()).isEqualTo("decimal");
        assertThat(column.getScale()).isEqualTo("4,2");
//        assertThat(column.isNotDefault()).isTrue();
        assertThat(column.getDefaultValue()).isEqualTo("NULL");
    }

    @Test
    void testToColumn_multipleAttributes() {
        String input = "`allows_gift_message` tinyint(1) NOT NULL AUTO_INCREMENT DEFAULT '0',";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("allows_gift_message");
        assertThat(column.getType()).isEqualTo("tinyint");
        assertThat(column.getScale()).isEqualTo("1");
        assertThat(column.isNotNull()).isTrue();
        assertThat(column.isAutoIncrement()).isTrue();
        assertThat(column.getDefaultValue()).isEqualTo("'0'");
    }

    @Test
    void testToColumn_smallInt() {
        String input = "  `id` int(1) NOT NULL AUTO_INCREMENT,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("id");
        assertThat(column.getType()).isEqualTo("int");
        assertThat(column.getScale()).isEqualTo("1");
    }

    @Test
    void testToColumn_int() {
        String input = "  `id` int(10) NOT NULL AUTO_INCREMENT,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("id");
        assertThat(column.getType()).isEqualTo("int");
        assertThat(column.getScale()).isEqualTo("10");
    }

    @Test
    void testToColumn_tinyint() {
        String input = "  `optimised` tinyint(1) NOT NULL DEFAULT '0',";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("optimised");
        assertThat(column.getType()).isEqualTo("tinyint");
        assertThat(column.getScale()).isEqualTo("1");
    }

    @Test
    void testToColumn_varchar() {
        String input = "  `name` varchar(255) NOT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("name");
        assertThat(column.getType()).isEqualTo("varchar");
        assertThat(column.getScale()).isEqualTo("255");
    }

    @Test
    void testToColumn_datetime() {
        String input = "  `created_at` datetime NOT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("created_at");
        assertThat(column.getType()).isEqualTo("datetime");
        assertThat(column.getScale()).isNull();
    }

    @Test
    void testToColumn_date() {
        String input =  "  `xmas_last_order_date_express` date DEFAULT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("xmas_last_order_date_express");
        assertThat(column.getType()).isEqualTo("date");
        assertThat(column.getScale()).isNull();
    }

    @Test
    void testToColumn_maxDecimal() {
        String input = "  `gross_price` decimal(65,30) DEFAULT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("gross_price");
        assertThat(column.getType()).isEqualTo("decimal");
        assertThat(column.getScale()).isEqualTo("65,30");
    }

    @Test
    void testToColumn_minDecimal() {
        String input = "  `specific_commission_rate` decimal(1,0) DEFAULT NULL,";
        Column column = parser.toColumn(input);
        assertThat(column).isNotNull();
        assertThat(column.getName()).isEqualTo("specific_commission_rate");
        assertThat(column.getType()).isEqualTo("decimal");
        assertThat(column.getScale()).isEqualTo("1,0");
    }

    @Test
    void testToColumn_whenNotAColumn() {
        String input = "CREATE TABLE `attribute_values` (";
        Column column = parser.toColumn(input);
        assertThat(column).isNull();
    }

    @Test
    void testToForeignKey() {
        String input = "   CONSTRAINT `product_details_ibfk_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`),";
        ForeignKey foreignKey = parser.toForeignKey(input);
        assertThat(foreignKey.getForeignKeyName()).isEqualTo("created_by_id");
        assertThat(foreignKey.getSourceTable()).isEqualTo("users");
        assertThat(foreignKey.getSourceColumn()).isEqualTo("id");
    }

    @Test
    void testToForeignKey_whenNotAForeignKey() {
        assertThat(parser.toForeignKey(" aBa ")).isNull();
    }

    @Test
    void testToPrimaryKey() {
        String input = "  PRIMARY KEY (`id`),";
        PrimaryKey primaryKey = parser.toPrimaryKey(input);
        assertThat(primaryKey.getKeyName()).isEqualTo("id");
    }

    @Test
    void testToPrimaryKey_whenNotAPrimaryKey() {
        assertThat(parser.toPrimaryKey(" aBa ")).isNull();
    }

    @Test
    void testToUniqueConstraint() {
        assertThat(parser.toUniqueConstraint(" aBa ")).isNull();
    }
}

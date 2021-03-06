package com.alexander.diagrams.db;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.PrimaryKey;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.model.UniqueConstraint;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlRegexParser implements DatabaseSyntaxParser {

    private static final String MYSQL_NAME_REGEX = "[a-zA-Z0-9\\-_]{0,64}";

    private static final String CREATE_TABLE_REGEX = ".*CREATE TABLE `(?<tablename>[a-zA-Z_\\-]*)` \\(";
    private static final String TABLE_NAME_GROUP = "tablename";
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(CREATE_TABLE_REGEX);

    @Override
    public Table toTable(String line) {
        String tableName = "";
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(line);
        if (matcher.matches()) {
            tableName = matcher.group(TABLE_NAME_GROUP);
        }
        return Table.builder().name(tableName).build();
    }
    //    private static final String ATTRIBUTES_REGEX =
    //      "((?<notnull> NOT NULL)?(?<autoincrement> AUTO_INCREMENT)?( DEFAULT (?<default>NULL|'[0-9]')?))";

    private static final String ATTRIBUTES_REGEX =
        "((?<notnull> NOT NULL)?(?<autoincrement> AUTO_INCREMENT)?( DEFAULT (?<default>NULL|'[0-9]'))?)";
    private static final String AUTO_INCREMENT_GROUP = "autoincrement";
    private static final String NOT_NULL_GROUP = "notnull";
    private static final String DEFAULT_GROUP = "default";

    // decimal scale (m,d) where m = 1 to 65 and d = 0 - 30
    // int scale maximum int (10)
    // varchar scale maximum (65,535) - using [0-9]{0,5} for brevity allowing numbers from 0 to 99,999
    // see {@link https://dev.mysql.com/doc/refman/8.0/en/char.html}
    private static final String NUMBER_SCALE_REGEX =
        "(\\((?<scale>([0-9]{0,5})|([0-9]|[1-5][0-9]|[6][1-5]),([0-9]|[1-2][0-9]|[3][0]))\\))?";
    private static final String SCALE_GROUP = "scale";

    private static final String DATATYPE_REGEX =
        "(?<datatype>int|varchar|tinyint|datetime|decimal|date)" + NUMBER_SCALE_REGEX;
    private static final String DATATYPE_GROUP = "datatype";

    private static final String COLUMN_REGEX =
        "\\s*`(?<columnName>[a-zA-Z-0-9\\-_]+)` " + DATATYPE_REGEX + ATTRIBUTES_REGEX + ",";
    private static final String COLUMN_NAME_GROUP = "columnName";

    private static final Pattern COLUMN_PATTERN = Pattern.compile(COLUMN_REGEX);

    @Override
    public Column toColumn(String line) {
        Matcher matcher = COLUMN_PATTERN.matcher(line);
        if (matcher.matches()) {
            return Column.builder()
                    .name(matcher.group(COLUMN_NAME_GROUP))
                    .type(matcher.group(DATATYPE_GROUP))
                    .scale(matcher.group(SCALE_GROUP))
                    .autoIncrement(matcher.group(AUTO_INCREMENT_GROUP) != null)
                    .notNull(matcher.group(NOT_NULL_GROUP) != null)
                    .defaultValue(matcher.group(DEFAULT_GROUP))
                    .build();
        }
        return null;
    }

    private static final String FOREIGN_KEY_REGEX  = "\\s*CONSTRAINT `" + MYSQL_NAME_REGEX
        + "` FOREIGN KEY \\(`(?<foreignKeyName>" + MYSQL_NAME_REGEX + ")`\\) "
        + "REFERENCES "
        + "`(?<sourceTable>" + MYSQL_NAME_REGEX + ")` \\(`(?<sourceColumn>" + MYSQL_NAME_REGEX + ")`\\).*";
    private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile(FOREIGN_KEY_REGEX);

    private static final String FOREIGN_KEY_NAME_GROUP = "foreignKeyName";
    private static final String SOURCE_TABLE_GROUP = "sourceTable";
    private static final String SOURCE_COLUMN_GROUP = "sourceColumn";

    /**
     * Converts a String representing a foreign key constraint from a describe table statement into a ForeignKey object.
     * @param line The String to convert into a ForeignKey object.
     * @return {@link ForeignKey}
     */
    public ForeignKey toForeignKey(String line) {
        Matcher matcher = FOREIGN_KEY_PATTERN.matcher(line);
        if (matcher.matches()) {
            return ForeignKey.builder()
                    .foreignKeyName(matcher.group(FOREIGN_KEY_NAME_GROUP))
                    .sourceTable(matcher.group(SOURCE_TABLE_GROUP))
                    .sourceColumn(matcher.group(SOURCE_COLUMN_GROUP))
                    .build();
        }
        return null;
    }

    private static final String PRIMARY_KEY_REGEX = "\\s*PRIMARY KEY \\((?<primaryKey>`" + MYSQL_NAME_REGEX
        + "`(,`" + MYSQL_NAME_REGEX + "`)*)\\),";
    private static final Pattern PRIMARY_KEY_PATTERN = Pattern.compile(PRIMARY_KEY_REGEX);
    private static final String PRIMARY_KEY_GROUP = "primaryKey";

    /**
     * Converts a String representing a primary key constraint from a describe table statement into a PrimaryKey object.
     * @param line The String to convert into a PrimaryKey object.
     * @return {@link PrimaryKey}
     */
    public PrimaryKey toPrimaryKey(String line) {
        Matcher matcher = PRIMARY_KEY_PATTERN.matcher(line);
        if (matcher.matches()) {
            String group = matcher.group(PRIMARY_KEY_GROUP);
            String key = group.replace("`", "");
            return PrimaryKey.builder()
                    .keyName(List.of(key.split(",")))
                    .build();
        }
        return null;
    }

    private static final String UNIQUE_CONSTRAINT_GROUP = "uniqueConstraint";
    private static final String UNIQUE_CONSTRAINT_REGEX = "(?<uniqueConstraint>\\s)";
    private static final Pattern UNIQUE_CONSTRAINT_PATTERN = Pattern.compile(UNIQUE_CONSTRAINT_REGEX);

    /**
     * Convert a string representing a describe table unique constraint into an object.
     * @param line The line to parse
     * @return {@link UniqueConstraint}
     */
    public UniqueConstraint toUniqueConstraint(String line) {
        Matcher matcher = UNIQUE_CONSTRAINT_PATTERN.matcher(line);
        if (matcher.matches()) {
            return UniqueConstraint.builder()
                .indexName(matcher.group(UNIQUE_CONSTRAINT_GROUP)).build();
        }
        return null;
    }
}

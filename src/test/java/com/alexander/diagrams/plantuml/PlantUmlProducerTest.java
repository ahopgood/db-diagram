package com.alexander.diagrams.plantuml;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlantUmlProducerTest {

    private PlantUmlProducer producer;
    private Table table;
    private ForeignKey foreignKey;

    @BeforeEach
    void setup() {
        producer = new PlantUmlProducer("My Diagram", "mydiagram.png");
        table = Table.builder()
                .name("Products")
                .columns(Arrays.asList(
                        Column.builder().name("Id").type("date").build(),
                        Column.builder().name("Name").type("varchar").scale("255").build()
                )).build();
        foreignKey = ForeignKey.builder()
            .foreignKeyName("Id")
            .sourceTable("People")
            .sourceColumn("Name")
            .build();
    }


    @Test
    void testGenerateDiagram_withNullForeignKeys() throws IOException {
        producer.generateDiagram(Arrays.asList(table));
    }

    @Test
    void testGenerateDiagram_withForreignKey() throws IOException {
        Table foreignTable = Table.builder()
            .name("Products")
            .columns(Arrays.asList(
                Column.builder().name("Id").type("date").build(),
                Column.builder().name("Name").type("varchar").scale("255").build()
            ))
            .build();

        Table table = Table.builder()
            .name("People")
            .columns(Arrays.asList(
                Column.builder().name("Id").type("date").build(),
                Column.builder().name("ProductName").type("varchar").scale("255").build()
            )).foreignKeys(Arrays.asList(ForeignKey.builder()
                .sourceColumn("Id")
                .sourceTable("Products")
                .foreignKeyName("ProductName")
                .build()))
            .build();

        producer.generateDiagram(Arrays.asList(foreignTable, table));
    }

    @Test
    void testTableFunction() {
        String tableString = producer.tableFunction(Table.builder().name("test").build());
        assertThat(tableString).isEqualTo("class test << (T," + PlantUmlProducer.COLOUR + ") >> {\n\n}\n");
    }

    @Test
    void testBuildColumns_withNullColumns() {
        String columnsString = producer.buildColumns(Table.builder().columns(
                Arrays.asList(Column.builder().build(), null)
        ).build());
        assertThat(columnsString);
    }

    @Test
    void testBuildColumns() {
        String columnsString = producer.buildColumns(Table.builder().columns(
                Arrays.asList(Column.builder().build())
        ).build());
        assertThat(columnsString);
    }

    @Test
    void testSet() {
        assertThat(producer.set("Test")).isEqualTo(" Test");
    }

    @Test
    void testSet_givenNull() {
        assertThat(producer.set(null)).isEqualTo("");
    }

    @Test
    void testBold() {
        assertThat(producer.bold("Test")).isEqualTo("<b>Test</b>");
    }

    @Test
    void testBold_givenNull() {
        assertThat(producer.bold(null)).isEqualTo("<b>null</b>");
    }

    @Test
    void testSetType_givenNullColumn() {
        assertThrows(NullPointerException.class, () -> producer.setType(null));
    }

    @Test
    void testSetType_givenColumnWithNoType() {
        Column column = Column.builder()
            .scale("2").build();
        assertThat(producer.setType(column)).isEqualTo("");
    }

    @Test
    void testSetType_givenColumnWithTypeOnly() {
        Column column = Column.builder().type("int").build();
        assertThat(producer.setType(column)).isEqualTo(" int");
    }

    @Test
    void testSetType_givenColumnWithScale() {
        Column column = Column.builder()
            .type("int")
            .scale("2")
            .build();
        assertThat(producer.setType(column)).isEqualTo(" int(2)");
    }

    @Test
    void testBuildColumn_withName() {
        String columnString = producer.buildColumn(Column.builder().name("Id").build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b>");
    }

    @Test
    void testBuildColumn_withDataType() {
        String columnString = producer.buildColumn(Column.builder().name("Id").type("varchar").build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> varchar");
    }

    @Test
    void testBuildColumn_withDataType_withScale() {
        String columnString = producer.buildColumn(Column.builder().name("Id").type("varchar").scale("255").build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> varchar(255)");
    }

    @Disabled
    @Test
    void testBuildColumn_withAutoIncrement() {
        String columnString = producer.buildColumn(Column.builder().name("Id").type("date").autoIncrement(true).build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> date auto increment");
    }

    @Disabled
    @Test
    void testBuildColumn_withNotNull() {
        String columnString = producer.buildColumn(Column.builder().name("Id").type("date").notNull(true).build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> date not null");
    }

    @Disabled
    @Test
    void testBuildColumn_withDefault() {
        String columnString = producer.buildColumn(Column.builder().name("Id").type("date").defaultValue("'0'").build());
        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> date default '0'");
    }

    @Test
    void testBuildForeignKey_givenNullForeignKey() {
        assertThrows(NullPointerException.class,
            () -> producer.buildForeignKey(
                null,
                table)
        );
    }

    @Test
    void testBuildForeignKey_givenTableIsNull() {
        assertThrows(NullPointerException.class,
            () -> producer.buildForeignKey(
                foreignKey,
                null)
        );
    }

    @Test
    void testBuildForeignKey_givenTableNameIsNull() {
        String foreignKeyString = producer.buildForeignKey(
            foreignKey,
            Table.builder().build());
        assertThat(foreignKeyString).isEqualTo("null::Id --> People::Name");
    }

    @Test
    void testBuildForeignKey_givenForeignKeyNameIsNull() {
        String foreignKeyString = producer.buildForeignKey(
            ForeignKey.builder()
                .sourceTable("People")
                .sourceColumn("Name")
                .build(),
            table);
        assertThat(foreignKeyString).isEqualTo("Products::null --> People::Name");
    }

    @Test
    void testBuildForeignKey_givenSourceColumnIsNull() {
        String foreignKeyString = producer.buildForeignKey(
            ForeignKey.builder()
                .foreignKeyName("Id")
                .sourceTable("People")
                .build(),
            table);
        assertThat(foreignKeyString).isEqualTo("Products::Id --> People::null");
    }

    @Test
    void testBuildForeignKey_givenSourceTableIsNull() {
        String foreignKeyString = producer.buildForeignKey(
            ForeignKey.builder()
                .foreignKeyName("Id")
                .sourceColumn("Name")
                .build(),
            table);
        assertThat(foreignKeyString).isEqualTo("Products::Id --> null::Name");
    }

    @Test
    void testBuildForeignKey() {
        String foreignKeyString = producer.buildForeignKey(
            foreignKey,
            table);
        assertThat(foreignKeyString).isEqualTo("Products::Id --> People::Name");
    }


    @Test
    void testBuildForeignKeys_whenTableNull() {
        assertThrows(NullPointerException.class,
            () -> producer.buildForeignKeys(null, Map.of()));
    }

    @Test
    void testBuildForeignKeys_whenForeignKeyListIsNull() {
        //Due to joining clause
        assertThat(producer.buildForeignKeys(Table.builder().build(), Map.of()))
            .isEqualTo("\n");
    }

    @Test
    void testBuildForeignKeys_whenForeignKeyIsNull() {
        List<ForeignKey> foreignKeys = new LinkedList<>();
        foreignKeys.add(null);

        assertThat(producer.buildForeignKeys(Table.builder()
                .foreignKeys(foreignKeys)
                .build(),
            Map.of()))
            .isEqualTo("\n");
    }

    @Test
    void testBuildForeignKeys_whenTableMapIsNull() {
        assertThrows(NullPointerException.class,
            () -> producer.buildForeignKeys(Table.builder()
                .foreignKeys(List.of(foreignKey))
                .build(),
            null));
    }

    @Test
    void testBuildForeignKeys_whenForeignKeySourceTableIsNotInMap() {
        assertThat(producer.buildForeignKeys(
            Table.builder()
                .foreignKeys(List.of(foreignKey))
                .build(),
            Map.of()))
        .isEqualTo("\n");
    }

    @Test
    void testBuildForeignKeys_whenShowForeignKeysIsFalse() {
        producer = new PlantUmlProducer("My Diagram", "mydiagram.png", false);
        table.setForeignKeys(List.of(foreignKey));
        assertThat(producer.buildForeignKeys(
            table,
            Map.of(foreignKey.getSourceTable(), table)))
            .isEqualTo("\n");
    }

    @Test
    void testBuildForeignKeys() {
        table.setForeignKeys(List.of(foreignKey));
        assertThat(producer.buildForeignKeys(
            table,
            Map.of(foreignKey.getSourceTable(), table)))
            .isEqualTo("Products::Id --> People::Name\n");
    }
}

package com.alexander.diagrams.plantuml;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class PlantUMLProducerTest {

    PlantUMLProducer producer;
    Table table;

    @BeforeEach
    void setup() {
        producer = new PlantUMLProducer("My Diagram", "mydiagram.png");
        table = Table.builder()
                .name("Products")
                .columns(Arrays.asList(
                        Column.builder().name("Id").type("date").build(),
                        Column.builder().name("Name").type("varchar").scale("255").build()
                )).build();
    }


    @Test
    void testGenerateDiagram() throws IOException {
        producer.generateDiagram(Arrays.asList(table));
    }

    @Test
    void testTableFunction() {
        String tableString = producer.tableFunction(Table.builder().name("test").build());
        assertThat(tableString).isEqualTo("class test << (T," + PlantUMLProducer.COLOUR + ") >> {\n\n}\n");
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

    @Disabled
    @Test
    void testBuildForeignKey_givenNullForeignKey() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }

    @Test
    void testBuildForeignKey_givenTableIsNull() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }

    @Test
    void testBuildForeignKey_givenForeignKeyNameIsNull() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }

    @Test
    void testBuildForeignKey_givenSourceColumnIsNull() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }

    @Test
    void testBuildForeignKey_givenSourceTableIsNull() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }

    @Test
    void testBuildForeignKey() {
        String foreignKeyString = producer.buildForeignKey(ForeignKey.builder().build(), Table.builder().build());
        fail("Not yet implemented");
    }


    @Test
    void testBuildForeignKeys_whenForeignKeySourceTableIsMissing() {
//        String columnString = producer.buildColumn(Column.builder().name("Id").type("date").defaultValue("'0'").build());
//        assertThat(columnString).isEqualTo("\t{field} <b>Id</b> date default '0'");
        fail("Not yet implemented");
    }
}
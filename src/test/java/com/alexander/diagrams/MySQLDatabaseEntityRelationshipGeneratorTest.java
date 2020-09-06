package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.PrimaryKey;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.plantuml.DiagramProducer;
import com.alexander.diagrams.source.Source;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class MySQLDatabaseEntityRelationshipGeneratorTest {

    private static final String testPath = "src/test/resources/";
    private static String packagePath;

    private DatabaseSyntaxParser parser = mock(DatabaseSyntaxParser.class);
    private DiagramProducer producer = mock(DiagramProducer.class);
    private Source source = mock(Source.class);

    private DatabaseEntityRelationshipGenerator generator
        = new DatabaseEntityRelationshipGenerator(parser, producer, source);

    private Optional<Table> tableOptional = Optional.of(Table.builder().build());

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";


    @Test
    void testGenerate() {
        when(source.hasNext()).thenReturn(true, false);
        when(source.next()).thenReturn(List.of());

        generator.generate();
        verify(source, times(2)).hasNext();
        verify(source, times(1)).next();
    }

    @Test
    void testToTable_givenEmptyList() {
        Optional<Table> table = generator.toTable(List.of());
        assertThat(table.isEmpty()).isTrue();
    }

    @Test
    void testToTable_givenNullTableGenerated() {
        when(parser.toTable(isA(String.class))).thenReturn(null);
        Optional<Table> table = generator.toTable(List.of("CREATE TABLE `test_table` ("));
        assertThat(table.isEmpty()).isTrue();
    }

    @Test
    void testToTable_givenOnlyHeader() {
        when(parser.toTable(isA(String.class))).thenReturn(Table.builder().name("test_table").build());
        Optional<Table> table = generator.toTable(List.of("CREATE TABLE `test_table` ("));
        assertThat(table.isPresent()).isTrue();
        assertThat(table.get().getName()).isEqualTo("test_table");
    }


    @Test
    void testAddColumns() {
        //Check we operate on the sublist
        //Check that parser.toColumns is called for each line
        //Check that we get back an Optional that is present
        //Check that we have a list of a specific size in our table object
        when(parser.toColumn(""))
                .thenReturn(Column.builder().name(KEY1).build())
                .thenReturn(Column.builder().name(KEY2).build());

        Optional<Table> table = generator.addColumns(Arrays.asList("",""), tableOptional);
        verify(parser, times(2)).toColumn("");

        assertThat(table.get().getColumns().size()).isEqualTo(2);
        assertThat(table.get().getColumns().get(0).getName()).isEqualTo(KEY1);
        assertThat(table.get().getColumns().get(1).getName()).isEqualTo(KEY2);
    }

    @Test
    void testAddColumns_whenNoColumnFound() {
        when(parser.toColumn("")).thenReturn(null);

        Optional<Table> table = generator.addColumns(Arrays.asList("",""), tableOptional);
        verify(parser, times(2)).toColumn("");

        assertThat(table.get().getColumns().size()).isEqualTo(0);
    }

    @Test
    void testAddForeignKey() {
        when(parser.toForeignKey(""))
                .thenReturn(ForeignKey.builder().foreignKeyName(KEY1).build())
                .thenReturn(ForeignKey.builder().foreignKeyName(KEY2).build());

        Optional<Table> table = generator.addForeignKey(Arrays.asList("",""), tableOptional);
        verify(parser, times(2)).toForeignKey("");

        assertThat(table.get().getForeignKeys().size()).isEqualTo(2);
        assertThat(table.get().getForeignKeys().get(0).getForeignKeyName()).isEqualTo(KEY1);
        assertThat(table.get().getForeignKeys().get(1).getForeignKeyName()).isEqualTo(KEY2);
    }

    @Test
    void testAddForeignKey_whenNoForeignKeyFound() {
        when(parser.toForeignKey(""))
                .thenReturn(null)
                .thenReturn(ForeignKey.builder().foreignKeyName(KEY2).build());

        Optional<Table> table = generator.addForeignKey(Arrays.asList("",""), tableOptional);
        verify(parser, times(2)).toForeignKey("");

        assertThat(table.get().getForeignKeys().size()).isEqualTo(1);
        assertThat(table.get().getForeignKeys().get(0).getForeignKeyName()).isEqualTo(KEY2);
    }

    @Test
    void testAddPrimaryKey() {
        when(parser.toPrimaryKey(""))
                .thenReturn(PrimaryKey.builder().keyName(KEY1).build())
                .thenReturn(PrimaryKey.builder().keyName(KEY2).build());

        Optional<Table> table = generator.addPrimaryKey(Arrays.asList("",""), tableOptional);
        verify(parser, times(2)).toPrimaryKey("");

        assertThat(table.get().getPrimaryKeys().size()).isEqualTo(2);
        assertThat(table.get().getPrimaryKeys().get(0).getKeyName()).isEqualTo(KEY1);
        assertThat(table.get().getPrimaryKeys().get(1).getKeyName()).isEqualTo(KEY2);
    }

    @Test
    void testAddPrimaryKey_whenNoPrimaryKeyFound() {
       when(parser.toPrimaryKey(""))
                .thenReturn(null)
                .thenReturn(PrimaryKey.builder().keyName(KEY2).build());

        Optional<Table> table = generator.addPrimaryKey(Arrays.asList("", ""), tableOptional);
        verify(parser, times(2)).toPrimaryKey("");

        assertThat(table.get().getPrimaryKeys().size()).isEqualTo(1);
        assertThat(table.get().getPrimaryKeys().get(0).getKeyName()).isEqualTo(KEY2);
    }

    @Test
    void testToDiagram_givenNullList() throws Exception {
        assertThrows(RuntimeException.class, () -> generator.toDiagram(null));
        verify(producer, times(0)).generateDiagram(null);
    }

    @Test
    void testToDiagram_givenEmptyList() throws Exception {
        assertThrows(RuntimeException.class, () -> generator.toDiagram(List.of()));
        verify(producer, times(0)).generateDiagram(anyList());
    }

    @Test
    void testToDiagram_givenList() throws Exception {
        generator.toDiagram(List.of(Table.builder().name("Test").build()));
        verify(producer, times(1)).generateDiagram(anyList());
    }

    @BeforeAll
    static void path() {
        packagePath = DatabaseEntityRelationshipGenerator.class.getPackageName()
                .replace(".", File.separator);
    }
}

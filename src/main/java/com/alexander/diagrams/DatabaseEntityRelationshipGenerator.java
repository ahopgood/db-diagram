package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.plantuml.DiagramProducer;
import com.alexander.diagrams.source.Source;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;

import static java.util.stream.Collectors.toList;

@Builder
public class DatabaseEntityRelationshipGenerator {

    private final DatabaseSyntaxParser parser;
    private final DiagramProducer producer;
    private final Source source;

    /**
     * Class responsible for converting database describe statements into objects and then into diagrams.
     * @param parser The {@link DatabaseSyntaxParser} to parse the database information from the Source
     * @param producer The {@link DiagramProducer} to convert the parsed database objects into a diagram
     * @param source The {@link Source} of database information
     */
    public DatabaseEntityRelationshipGenerator(DatabaseSyntaxParser parser,
                                               DiagramProducer producer,
                                               Source source) {
        this.parser = parser;
        this.producer = producer;
        this.source = source;
    }

    /**
     * Generate a diagram using the provided {@link Source}.
     */
    public void generate() throws Exception {
        List<Optional<Table>> tables = new LinkedList<>();
        while (source.hasNext()) {
            tables.add(toTable(source.next()));
        }
        toDiagram(tables.stream()
            .filter(table -> table.isPresent())
            .map(table -> table.get())
            .collect(toList()));
    }

    /**
     * Convert a line of lines representing a database create statement into a Table object.
      * @param lines The List of Strings to convert to a table.
     * @return {@link Table}
     */
    Optional<Table> toTable(List<String> lines) {
        Optional<Table> table = lines
                .stream()
                .limit(1)
                .map(s -> parser.toTable(s))
                .filter(Objects::nonNull)
                .findFirst();
        table = addColumns(lines, table);
        table = addForeignKey(lines, table);
        return table;
    }

    Optional<Table> addColumns(List<String> lines, Optional<Table> table) {
        table.ifPresent(t -> t.setColumns(
                lines.stream()
                        .map(s -> parser.toColumn(s))
                        .filter(Objects::nonNull)
                        .collect(toList())
        ));
        return table;
    }

    Optional<Table> addForeignKey(List<String> lines, Optional<Table> table) {
        List<ForeignKey> foreignKeys = lines.stream()
            .map(s -> parser.toForeignKey(s))
            .filter(Objects::nonNull)
            .collect(toList());

        table.ifPresent(t -> t.setForeignKeys(foreignKeys));

        //Mark columns as foreign keys
        List<String> names = foreignKeys.stream().map(key -> key.getForeignKeyName()).collect(toList());
        List<Column> columns = table.orElse(Table.builder().build())
            .getColumns().stream().map(column -> map(column, names)).collect(toList());
        table.ifPresent(t -> t.setColumns(columns));
        return table;
    }

    private Column map(Column column, List<String> keyNames) {
        boolean isForeign = keyNames.contains(column.getName());
        return Column.builder()
            .name(column.getName())
            .scale(column.getScale())
            .type(column.getType())
            .autoIncrement(column.isAutoIncrement())
            .notDefault(column.isNotDefault())
            .defaultValue(column.getDefaultValue())
            .primary(column.isPrimary())
            .notNull(column.isNotNull())
            .foreign(isForeign)
            .build();
    }

    Optional<Table> addPrimaryKey(List<String> lines, Optional<Table> table) {
        table.ifPresent(t -> t.setPrimaryKeys(
                lines.stream()
                        .map(s -> parser.toPrimaryKey(s))
                        .filter(Objects::nonNull)
                        .collect(toList())
        ));
        return table;
    }

    /**
     * Converts a parsed list of tables into a diagram using the provided {@link DiagramProducer}.
     * @param tables a list of {@link Table}s to convert into a diagram
     * @throws Exception catch-all Exception handling
     */
    void toDiagram(List<Table> tables) throws Exception {
        Optional.ofNullable(tables)
            .orElseThrow(() -> new RuntimeException("Unable to convert null Table list to a diagram"));
        if (tables.size() > 0) {
            producer.generateDiagram(tables);
        } else {
            throw new RuntimeException("Unable to convert empty Table list to a diagram");
        }
    }
}

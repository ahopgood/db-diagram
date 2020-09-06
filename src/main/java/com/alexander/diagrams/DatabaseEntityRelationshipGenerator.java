package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.db.MySqlRegexParser;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.plantuml.DiagramProducer;
import com.alexander.diagrams.plantuml.PlantUmlProducer;
import com.alexander.diagrams.source.Source;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DatabaseEntityRelationshipGenerator {

    private final DatabaseSyntaxParser parser;
    private final DiagramProducer producer;
    private final Source source;

    public static DatabaseEntityRelationshipGenerator getMySqlGenerator(String diagramTitle, String outputFile, Source source) {
        return new DatabaseEntityRelationshipGenerator(new MySqlRegexParser(),
            new PlantUmlProducer(diagramTitle, outputFile, true),
            source);
    }

    public DatabaseEntityRelationshipGenerator(DatabaseSyntaxParser parser,
                                               DiagramProducer producer,
                                               Source source) {
        this.parser = parser;
        this.producer = producer;
        this.source = source;
    }

    /**
     * Convert a line of lines representing a database create statement into a Table object.
      * @param lines The List of Strings to convert to a table.
     * @return {@link Table}
     */
    public Optional<Table> toTable(List<String> lines) {
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
        table.ifPresent(t -> t.setForeignKeys(
                lines.stream()
                        .map(s -> parser.toForeignKey(s))
                        .filter(Objects::nonNull)
                        .collect(toList())
        ));
        return table;
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

    public void toDiagram(List<Table> tables) throws Exception {
        producer.generateDiagram(tables);
    }
}

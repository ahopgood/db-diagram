package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.db.MySqlRegexParser;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.plantuml.DiagramProducer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DatabaseEntityRelationshipGenerator {

    private DatabaseSyntaxParser parser;
    private DiagramProducer producer;

    public static DatabaseEntityRelationshipGenerator getMySqlGenerator(String diagramTitle, String outputFile) {
        return new DatabaseEntityRelationshipGenerator(new MySqlRegexParser(),
            new com.alexander.diagrams.plantuml.PlantUmlProducer(diagramTitle, outputFile, true));
    }

    public DatabaseEntityRelationshipGenerator(DatabaseSyntaxParser parser, DiagramProducer producer) {
        this.parser = parser;
        this.producer = producer;
    }

    public List<String> read(Path file) throws IOException {
        List<String> lines  = Files.readAllLines(file);
        return lines;
    }

    /**
     * Convert a line of lines representing a database create statement into a Table object.
      * @param lines The List of Strings to convert to a table.
     * @return {@link Table}
     */
    public Optional<Table> toTable(List<String> lines) {
        Optional<Table> table = lines.subList(0, 1)
                .stream()
                .map(s -> parser.toTable(s))
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
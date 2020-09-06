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

    /**
     * Helper method for wiring up a MySqlRegexParser and PlantUmlProducer with an input Source.
     * @param diagramTitle the title to put at the top of the diagram (plant uml specific field)
     * @param outputFile The file to write the diagram to
     * @param source The {@link Source} to use as input data for the diagram
     * @return a DatabaseEntityRelationshipGenerator wired up with a {@MySqlRegexParser} and a {@link PlantUmlProducer}
     */
    public static DatabaseEntityRelationshipGenerator getMySqlGenerator(String diagramTitle,
                                                                        String outputFile,
                                                                        Source source) {
        return new DatabaseEntityRelationshipGenerator(new MySqlRegexParser(),
            new PlantUmlProducer(diagramTitle, outputFile, true),
            source);
    }

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
    public void generate() {
        while (source.hasNext()) {
            source.next();
        }
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

    /**
     * Converts a parsed list of tables into a diagram using the provided {@link DiagramProducer}.
     * @param tables a list of {@link Table}s to convert into a diagram
     * @throws Exception catch-all Exception handling
     */
    public void toDiagram(List<Table> tables) throws Exception {
        Optional.ofNullable(tables)
            .orElseThrow(() -> new RuntimeException("Unable to convert null Table list to a diagram"));
        if (tables.size() > 0) {
            producer.generateDiagram(tables);
        } else {
            throw new RuntimeException("Unable to convert empty Table list to a diagram");
        }
    }
}

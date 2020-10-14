package com.alexander.diagrams.plantuml;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

@Builder
@SuppressFBWarnings(value = {"WEAK_FILENAMEUTILS", "PATH_TRAVERSAL_OUT", "UPM_UNCALLED_PRIVATE_METHOD"},
    justification = "WEAK_FILENAMEUTILS: Null byte injection is fixed in Java 7u40 and higher https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8014846. "
        + "PATH_TRAVERSAL_OUT FilenameUtils.getName() strips out the path from the filename preventing path traversal,"
        + " the file will be written to a location relative to the running code."
        + "UPM_UNCALLED_PRIVATE_METHOD: Lombok default reflective calls.")
/*
 * Class to create a PlantUML diagram.
 * @param title The diagram title
 * @param filename The name of the output file, location relative to executing code
 * @param showOrphanForeignKeys toggles whether or not to show foreign key relationships that don't have a table
 *                              known to the producer, these orphan relationships will often point to an empty
 *                              table.
 * @param plantUmlLimitSize maximum resolution size of the generated image; 4096 by default translating to an image of
 *                          4096x4096 (16,777,216) pixels.
 * @param generatePlantUmlFile boolean, defaults to false. Indicates if a plantuml file should be generated
 * using the filename parameter
*/
public class PlantUmlProducer implements DiagramProducer {

    private final String title;
    private final String filename;
    @Builder.Default
    private final OutputFileFormat outputFileFormat = OutputFileFormat.PNG;
    @Builder.Default
    private final boolean showOrphanForeignKeys = false;
    @Builder.Default
    private final int plantUmlLimitSize = 4096;
    @Builder.Default
    private final boolean generatePlantUmlFile = false;

    /** File extension strings. */
    private static final String PLANTUML_EXT = ".puml";
    private static final String PNG_EXT = ".png";
    private static final String SVG_EXT = ".svg";
    /** Diagram related values. */
    private static final String PLANTUML_LIMIT_SIZE_KEY = "PLANTUML_LIMIT_SIZE";
    private static final String START = "@startuml";
    private static final String END = "@enduml";
    private static final String TITLE = "Title: %s";

    /**
     * Creates a diagram based on the constructor args from the input table data.
     * @param tables a List of tables
     */
    public void generateDiagram(List<Table> tables) {
        StringBuilder diagramSource = new StringBuilder();
        diagramSource.append(START).append(NEWLINE);
        diagramSource.append(String.format(TITLE, title)).append(NEWLINE);
        for (Table table : tables) {
            diagramSource.append(tableFunction(table));
        }

        Map<String, Table> tableMap = toMap(tables);
        diagramSource.append(
            tables.stream().map(table -> buildForeignKeys(table, tableMap))
                .filter(string -> !string.trim().isEmpty())
                .collect(joining("", "", NEWLINE))
        );
        diagramSource.append(END).append(NEWLINE);

        generatePlantUml(diagramSource);

        System.setProperty(PLANTUML_LIMIT_SIZE_KEY, "" + plantUmlLimitSize);

        generateDiagramFile(diagramSource);
    }

    Map<String, Table> toMap(List<Table> tables) {
        return tables.stream().collect(Collectors.toMap(Table::getName, Function.identity()));
    }

    private static final String NEWLINE = "\n";
    private static final String TAB = "\t";
    private static final String SPACE = " ";
    private static final String FIELD = "{field}";
    private static final String RIGHT_BRACE = "}";
    protected static final String COLOUR = "#FFAAAA";
    private static final String TABLE = "class %s << (T," + COLOUR + ") >> {";
    private static final String FOREIGN_KEY = "<<FK>>";
    private static final String PRIMARY_KEY = "<<PK>>";

    protected String tableFunction(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(TABLE, table.getName())).append(NEWLINE);
        builder.append(buildColumns(table));
        builder.append(RIGHT_BRACE).append(NEWLINE);
        return builder.toString();
    }

    protected String buildColumns(Table table) {
        return Optional.ofNullable(table.getColumns())
                .orElseGet(() -> new LinkedList<>())
                .stream()
                .filter(column -> nonNull(column))
                .map(this::buildColumn)
                .collect(joining(NEWLINE, "", NEWLINE));
    }

    protected String buildColumn(Column column) {
        StringBuilder builder = new StringBuilder();

        builder.append(TAB)
            .append(FIELD)
            .append(set(bold(column.getName())))
            .append(setType(column));

        if (column.isForeign()) {
            builder.append(set(FOREIGN_KEY));
        }

        if (column.isPrimary()) {
            builder.append(set(PRIMARY_KEY));
        }
        return builder.toString();
    }

    String set(String value) {
        if (value == null) {
            return "";
        } else {
            return SPACE + value;
        }
    }

    String bold(String value) {
        return "<b>" + value + "</b>";
    }

    String setType(Column column) {
        if (column.getType() != null) {
            if (column.getScale() == null) {
                return SPACE + column.getType();
            } else {
                return SPACE + column.getType() + "(" + column.getScale() + ")";
            }
        } else {
            return "";
        }
    }

    protected String buildForeignKeys(Table table, Map<String, Table> tableMap) {
        return Optional.ofNullable(table.getForeignKeys())
                .orElseGet(() -> new LinkedList<>())
                .stream()
                .filter(foreignKey -> nonNull(foreignKey))
                .filter(foreignKey -> nonNull(tableMap.get(foreignKey.getSourceTable())) || showOrphanForeignKeys)
                .map(fk -> buildForeignKey(fk, table))
                .collect(joining(NEWLINE, "", NEWLINE));
    }

    protected String buildForeignKey(ForeignKey foreignKey, Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append(table.getName() + "::" + foreignKey.getForeignKeyName()
            + " -- " + foreignKey.getSourceTable() + "::" + foreignKey.getSourceColumn());
        return builder.toString();
    }

    protected void generatePlantUml(StringBuilder diagramSource) {
        if (generatePlantUmlFile) {
            Optional.ofNullable(filename)
                .orElseThrow(() ->
                    new RuntimeException("A filename is required to generate a " + PLANTUML_EXT + " file."));
            try {
                Path file = Path.of(filename + PLANTUML_EXT);
                BufferedWriter writer = Files
                    .newBufferedWriter(file, Charsets.toCharset("UTF-8"));
                try {
                    writer.write(diagramSource.toString());
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create plantuml file for filename: " + filename, e);
            }
        } else {
            System.out.println(diagramSource.toString());
        }
    }

    protected void generateDiagramFile(StringBuilder diagramSource) {
        generateDiagramFile(diagramSource, outputFileFormat);
    }

    private void generateDiagramFile(StringBuilder diagramSource, OutputFileFormat format) {
        String suffix = format.getPlantUmlFormat().getFileSuffix();
        Optional.ofNullable(filename)
            .orElseThrow(() ->
                new RuntimeException("A filename is required to generate a " + suffix + " file."));

        SourceStringReader reader = new SourceStringReader(diagramSource.toString());
        String outputFile = FilenameUtils.getName(filename + suffix);

        try (OutputStream os = new FileOutputStream(Paths.get(outputFile).toString())) {
            reader.generateImage(os, new FileFormatOption(format.getPlantUmlFormat()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

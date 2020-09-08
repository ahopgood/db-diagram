package com.alexander.diagrams.plantuml;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.FilenameUtils;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

@Builder
@SuppressFBWarnings(value = {"WEAK_FILENAMEUTILS", "PATH_TRAVERSAL_OUT"},
    justification = "WEAK_FILENAMEUTILS: Null byte injection is fixed in Java 7u40 and higher https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8014846. "
        + "PATH_TRAVERSAL_OUT FilenameUtils.getName() strips out the path from the filename preventing path traversal,"
        + " the file will be written to a location relative to the running code.")
public class PlantUmlProducer implements DiagramProducer {

    private final String title;
    private final String filename;
    private final boolean showOrphanForeignKeys;

    /**
     * Class to create a PlantUML diagram.
     * @param title The diagram title
     * @param filename The name of the output file, location relative to executing code
     */
    public PlantUmlProducer(String title, String filename) {
        this.title = title;
        this.filename = FilenameUtils.getName(filename);
        this.showOrphanForeignKeys = true;
    }

    /**
     * Class to create a PlantUML diagram.
     * @param title The diagram title
     * @param filename The name of the output file, location relative to executing code
     * @param showOrphanForeignKeys toggles whether or not to show foreign key relationships that don't have a table
     *                              known to the producer, these orphan relationships will often point to an empty
     *                              table.
     */
    public PlantUmlProducer(String title, String filename, boolean showOrphanForeignKeys) {
        this.title = title;
        this.filename = FilenameUtils.getName(filename);
        this.showOrphanForeignKeys = showOrphanForeignKeys;
    }

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
                .filter(string -> nonNull(string) && !string.trim().isEmpty())
                .collect(joining("", "", NEWLINE))
        );
        diagramSource.append(END).append(NEWLINE);

        System.out.println(diagramSource.toString());
        SourceStringReader reader = new SourceStringReader(diagramSource.toString());
        try (OutputStream png = new FileOutputStream(Paths.get(FilenameUtils.getName(filename)).toString())) {
            reader.generateImage(png);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            + " --> " + foreignKey.getSourceTable() + "::" + foreignKey.getSourceColumn());
        return builder.toString();
    }
}

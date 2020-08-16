package com.alexander.diagrams.plantuml;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.Table;
import net.sourceforge.plantuml.SourceStringReader;

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

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

public class PlantUMLProducer implements DiagramProducer {

    private final String title;
    private final String filename;
    private final boolean showForeignKeys;

    public PlantUMLProducer(String title, String filename) {
        this.title = title;
        this.filename = filename;
        this.showForeignKeys = true;
    }

    public PlantUMLProducer(String title, String filename, boolean showForeignKeys) {
        this.title = title;
        this.filename = filename;
        this.showForeignKeys = showForeignKeys;
    }


    private static final String START = "@startuml";
    private static final String END = "@enduml";
    private static final String TITLE = "Title: %s";

    public void generateDiagram(List<Table> tables) throws IOException {
        Map<String, Table> tableMap = toMap(tables);
        OutputStream png = new FileOutputStream(Paths.get(filename).toString());

        StringBuilder diagramSource = new StringBuilder();
        diagramSource.append(START).append(NEWLINE);
        diagramSource.append(String.format(TITLE, title)).append(NEWLINE);
        for (Table table : tables) {
            diagramSource.append(tableFunction(table));
        }
        diagramSource.append(
                tables.stream().map(table -> buildForeignKeys(table, tableMap))
                        .filter(string -> nonNull(string) && !string.trim().isEmpty())
                        .collect(joining("", "", NEWLINE))
        );
        diagramSource.append(END).append(NEWLINE);

        System.out.println(diagramSource.toString());
        SourceStringReader reader = new SourceStringReader(diagramSource.toString());
        reader.generateImage(png);
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

    private String set(String value) {
        if (value == null) {
            return "";
        } else {
            return SPACE + value;
        }
    }

    private String bold(String value){
        return "<b>" + value + "</b>";
    }

    private String setType(Column column) {
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
                .filter(foreignKey -> nonNull(tableMap.get(foreignKey.getSourceTable())) && showForeignKeys)
                .map(fk -> buildForeignKey(fk, table))
                .collect(joining(NEWLINE, "", NEWLINE));
    }

    protected String buildForeignKey(ForeignKey foreignKey, Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append(table.getName() + "::" + foreignKey.getForeignKeyName() + " --> " + foreignKey.getSourceTable() + "::" + foreignKey.getSourceColumn());
        return builder.toString();
    }
}

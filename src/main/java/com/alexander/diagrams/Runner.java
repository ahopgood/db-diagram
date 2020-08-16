package com.alexander.diagrams;

import com.alexander.diagrams.model.Table;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Runner {
    public static void main(String[] args) throws Exception {

        String testPath = "src/test/resources/";
        String packagePath = DbERGenerator.class.getPackageName()
                .replace(".", File.separator);

        String system = "pim";

        DbERGenerator dbERGenerator = DbERGenerator.getMySQLGenerator(system, system + ".png");

        List<Optional<Table>> tables = new LinkedList<>();
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(Path.of(testPath, packagePath, system), "*.sql")) {
            for (Path file : dir) {
                tables.add(dbERGenerator.toTable(dbERGenerator.read(file)));
            }
        }
        //Convert multiple tables into a single diagram
        dbERGenerator.toDiagram(tables.stream()
                .filter(table -> table.isPresent())
                .map(table -> table.get())
                .collect(Collectors.toList()));
    }
}

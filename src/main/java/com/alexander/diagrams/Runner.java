package com.alexander.diagrams;

import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.source.FileSource;
import com.alexander.diagrams.source.Source;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Runner {
    /**
     * A main (temporary) entry point for integration testing.
     * @param args not used
     * @throws Exception any Runtime Exception
     */
    public static void main(String[] args) throws Exception {

        String testPath = "src/test/resources/";
        String packagePath = DatabaseEntityRelationshipGenerator.class.getPackageName()
                .replace(".", File.separator);

        String system = "pim";

        List<Optional<Table>> tables = new LinkedList<>();
        Source source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .build();

        DatabaseEntityRelationshipGenerator databaseEntityRelationshipGenerator =
            DatabaseEntityRelationshipGenerator.getMySqlGenerator(system, system + ".png", source);

        while (source.hasNext()) {
            tables.add(databaseEntityRelationshipGenerator.toTable(source.next()));
        }

        //Convert multiple tables into a single diagram
        databaseEntityRelationshipGenerator.toDiagram(tables.stream()
                .filter(table -> table.isPresent())
                .map(table -> table.get())
                .collect(Collectors.toList()));
    }
}

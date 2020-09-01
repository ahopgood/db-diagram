package com.alexander.diagrams;

import com.alexander.diagrams.model.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
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
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", justification = "https://github.com/spotbugs/spotbugs/issues/756")
    public static void main(String[] args) throws Exception {

        String testPath = "src/test/resources/";
        String packagePath = DatabaseEntityRelationshipGenerator.class.getPackageName()
                .replace(".", File.separator);

        String system = "pim";

        DatabaseEntityRelationshipGenerator databaseEntityRelationshipGenerator =
            DatabaseEntityRelationshipGenerator.getMySqlGenerator(system, system + ".png");

        List<Optional<Table>> tables = new LinkedList<>();
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(Path.of(testPath, packagePath, system), "*.sql")) {
            for (Path file : dir) {
                tables.add(databaseEntityRelationshipGenerator.toTable(databaseEntityRelationshipGenerator.read(file)));
            }
        }
        //Convert multiple tables into a single diagram
        databaseEntityRelationshipGenerator.toDiagram(tables.stream()
                .filter(table -> table.isPresent())
                .map(table -> table.get())
                .collect(Collectors.toList()));
    }
}

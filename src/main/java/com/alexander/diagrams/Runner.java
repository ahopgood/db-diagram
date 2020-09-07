package com.alexander.diagrams;

import com.alexander.diagrams.source.FileSource;
import com.alexander.diagrams.source.Source;
import java.io.File;
import java.nio.file.Path;

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

        Source source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .build();

        DatabaseEntityRelationshipGenerator databaseEntityRelationshipGenerator =
            DatabaseEntityRelationshipGenerator.getMySqlGenerator(system, system + ".png", source);
        databaseEntityRelationshipGenerator.generate();
    }
}

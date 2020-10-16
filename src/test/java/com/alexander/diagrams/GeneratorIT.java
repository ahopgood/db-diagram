package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.db.MySqlRegexParser;
import com.alexander.diagrams.diagram.DiagramProducer;
import com.alexander.diagrams.diagram.PlantUmlProducer;
import com.alexander.diagrams.generator.DatabaseEntityRelationshipGenerator;
import com.alexander.diagrams.source.FileSource;
import com.alexander.diagrams.source.Source;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class GeneratorIT {

    @Test
    void mainIntTest() throws Exception {

        String testPath = "src/test/resources/";
        String packagePath = DatabaseEntityRelationshipGenerator.class.getPackageName()
            .replace(".", File.separator);

        String system = "pim";

        Source source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .build();

        DiagramProducer producer = PlantUmlProducer.builder()
            .filename(system + ".png")
            .title(system)
            .showOrphanForeignKeys(true)
            .build();

        DatabaseSyntaxParser parser = new MySqlRegexParser();

        DatabaseEntityRelationshipGenerator generator = DatabaseEntityRelationshipGenerator.builder()
            .parser(parser)
            .producer(producer)
            .source(source)
            .build();

        generator.generate();
    }



}

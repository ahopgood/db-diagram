package com.alexander.diagrams;

import com.alexander.diagrams.db.DatabaseSyntaxParser;
import com.alexander.diagrams.diagram.DiagramProducer;
import com.alexander.diagrams.generator.Generator;
import com.alexander.diagrams.source.Source;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOptions;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchitectureIT {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup(){
        importedClasses = new ClassFileImporter(new ImportOptions().with(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS))
            .importPackages("com.alexander.diagrams");
    }

    @Test
    @DisplayName("Sources can only be accessed by Generators")
    void testSources() {
        ArchRule sourcePackageRule = classes()
            .that().implement(Source.class)
            .should().resideInAPackage("..source..");
        sourcePackageRule.check(importedClasses);

        ArchRule sourceAccessRule = classes()
            .that().resideInAPackage("..source..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..generator..", "..source..");
        sourceAccessRule.check(importedClasses);
    }

    @Test
    @DisplayName("Generators")
    void testGenerator() {
        ArchRule generatorPackageRule = classes()
            .that().implement(Generator.class)
            .should().resideInAPackage("..generator..");
        generatorPackageRule.check(importedClasses);

//        ArchRule generatorAccessRule = classes()
//            .that().resideInAPackage("..generator..")
//            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..generator..", "..source..");
//        generatorAccessRule.check(importedClasses);
    }

    @Test
    @DisplayName("DatabaseSyntaxParser is only accessed by Generators")
    void testDatabaseSyntaxParser() {
        ArchRule syntaxParserRule = classes()
            .that().implement(DatabaseSyntaxParser.class)
            .should().resideInAPackage("..db..");
        syntaxParserRule.check(importedClasses);

        ArchRule syntaxParserAccessRule = classes()
            .that().resideInAPackage("..db..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..generator..", "..db..");
        syntaxParserAccessRule.check(importedClasses);
    }

    @Test
    @DisplayName("DiagramProducer is only accessed by Generators")
    void testDiagramProducer() {
        ArchRule diagramProducerRule = classes()
            .that().implement(DiagramProducer.class)
            .should().resideInAPackage("..diagram..");
        diagramProducerRule.check(importedClasses);

        ArchRule syntaxParserAccessRule = classes()
            .that().resideInAPackage("..diagram..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..generator..", "..diagram..");
        syntaxParserAccessRule.check(importedClasses);

    }

    @Test
    @DisplayName("Ensure service layers are respected")
    void testLayers() {
        layeredArchitecture()
            .layer("Generator").definedBy("..generator..")
            .layer("Model").definedBy("..model..")
            .layer("Source").definedBy("..source..")
            .layer("Diagram").definedBy("..diagram..")
            .layer("Database").definedBy("..db..")

            .whereLayer("Generator").mayNotBeAccessedByAnyLayer()
            .whereLayer("Source").mayOnlyBeAccessedByLayers("Generator")
            .whereLayer("Diagram").mayOnlyBeAccessedByLayers("Generator")
            .whereLayer("Database").mayOnlyBeAccessedByLayers("Generator")
            .whereLayer("Model").mayOnlyBeAccessedByLayers("Generator", "Diagram", "Database")
            .check(importedClasses);
    }

}

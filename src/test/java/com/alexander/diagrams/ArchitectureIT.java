package com.alexander.diagrams;

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

public class ArchitectureIT {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup(){
        importedClasses = new ClassFileImporter(new ImportOptions().with(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS))
            .importPackages("com.alexander.diagrams");
    }

    @Test
    @DisplayName("Sources can only be accessed by Generators")
    void testPackages() {
        ArchRule sourcePackageRule = classes()
            .that().resideInAPackage("..sources..")
            .and().implement(Source.class)
            .should().onlyBeAccessed().byAnyPackage("..generator..");

        sourcePackageRule.check(importedClasses);
    }
}

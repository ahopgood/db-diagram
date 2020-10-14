package com.alexander.diagrams.plantuml;

import java.util.stream.Stream;
import net.sourceforge.plantuml.FileFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;


import static org.assertj.core.api.Assertions.assertThat;

class OutputFileFormatTest {


    @MethodSource("createFileArguments")
    @ParameterizedTest
    void testPng(OutputFileFormat inputFormat, FileFormat expectedFormat) {
        assertThat(inputFormat.getPlantUmlFormat()).isEqualTo(expectedFormat);
    }

    private static Stream<Arguments> createFileArguments() {
        return Stream.of(
            Arguments.of(OutputFileFormat.PNG, FileFormat.PNG),
            Arguments.of(OutputFileFormat.SVG,  FileFormat.SVG)
        );
    }
}

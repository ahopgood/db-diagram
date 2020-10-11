package com.alexander.diagrams.source;

import com.alexander.diagrams.DatabaseEntityRelationshipGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class FileSourceTest {

    static String testPath = String.format("src%stest%sresources%s",
        File.separator, File.separator, File.separator);
    static String packagePath = DatabaseEntityRelationshipGenerator.class.getPackageName()
        .replace(".", File.separator);
    static String system = "pim";

    @AfterAll
    public static void tearDown() {
        File tempFile = Path.of(testPath, packagePath, system, "temp.sql").toFile();
        if (tempFile.exists()) {
            Files.delete(tempFile);
        }
    }


    @Test
    void testGetContents_givenNullDirectoryString() {
        assertThrows(IllegalArgumentException.class,
            () -> FileSource.builder().build());
    }

    @Test
    void testGetContents_givenInvalidDirectoryString() {
        assertThrows(RuntimeException.class,
            () -> FileSource.builder()
                .directoryPath(Path.of("/madeup").toString())
                .fileName("*.sql")
                .build());
    }

    @Test
    void testGetContents_givenGlobFilename() throws IOException {
        FileSource source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .fileName("*.sql")
            .build();
        assertTrue(source.hasNext());
        assertSourceSize(8, source);
    }

    @Test
    void testGetContents_givenPathInvalid() {
        FileSource source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .fileName("doesnotexist.sql")
            .build();
        assertFalse(source.hasNext());
        assertSourceSize(0, source);
    }

    @Test
    void testGetContents_givenDirectoryAndNoFilename_thenWildcard() {
        FileSource source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .build();
        assertTrue(source.hasNext());
        assertSourceSize(8, source);
    }

    @Test
    void testGetContents_givenFile() {
        FileSource source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .fileName("inventory.sql")
            .build();
        assertTrue(source.hasNext());
        assertSourceSize(1, source);
    }

    @Test
    void testNext() {
        Files.newFile(Path.of(testPath, packagePath, system, "temp.sql").toString());
        FileSource source = FileSource.builder()
            .directoryPath(Path.of(testPath, packagePath, system).toString())
            .fileName("temp.sql")
            .build();
        Files.delete(Path.of(testPath, packagePath, system, "temp.sql").toFile());
        assertThrows(RuntimeException.class, () -> source.next());
    }

    private void assertSourceSize(int expectedSize, Source source) {
        int i = 0;
        while (source.hasNext()) {
            source.next();
            i++;
        }
        assertEquals(expectedSize, i);
    }
}

package com.alexander.diagrams.source;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

/**
 * Sources database describe statements from a directory and optionally from a specific file.
 */
@SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", justification = "https://github.com/spotbugs/spotbugs/issues/756")
public class FileSource implements Source {

    private final String directoryPath;
    private final String fileName;
    private List<Path> files;
    private Iterator<Path> iterator;

    /**
     * A source that reads files from a directory, supports glob searching.
     * @param directoryPath The string representing a directory to search for .sql files containing describe statements
     *                      to parse
     * @param fileName A specific filename in the above directory to read, can be a glob or if not specified then *.sql
     *                 is used
     */
    @Builder
    public FileSource(String directoryPath, String fileName) {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
        init();
    }

    private void init() {
        Optional.ofNullable(directoryPath).orElseThrow(
            () -> new IllegalArgumentException("The provided directory path is null"));

        try (DirectoryStream<Path> dir = Files.newDirectoryStream(Path.of(directoryPath),
            Optional.ofNullable(fileName).orElse("*.sql"))) {
            files = new LinkedList<>();
            for (Path file : dir) {
                files.add(file);
            }
            iterator = files.iterator();
        } catch (IOException e) {
            throw new RuntimeException("There was an issue accessing directory" + directoryPath, e);
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public List<String> next() {
        Path file = iterator.next();
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file " + file.toString(), e);
        }
    }
}

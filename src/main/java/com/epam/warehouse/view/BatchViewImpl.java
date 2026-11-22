package com.epam.warehouse.view;

import com.epam.warehouse.common.AppInfo;
import com.epam.warehouse.controller.Controller;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

/**
 * Runs the requests written in a text file, one per line, and prints each with its answer.
 * Blank lines and lines starting with {@code #} are skipped.
 */
public final class BatchViewImpl extends AbstractView {

    private static final String PROMPT = "> ";
    private static final String COMMENT_PREFIX = "#";

    private final Path file;
    private Iterator<String> lines;

    public BatchViewImpl(Controller controller, AppInfo appInfo, Path file) {
        this(controller, appInfo, file, System.out);
    }

    public BatchViewImpl(Controller controller, AppInfo appInfo, Path file, PrintStream out) {
        super(controller, appInfo, out);
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    protected String nextLine() {
        if (lines == null) {
            lines = readRequests();
        }
        if (!lines.hasNext()) {
            return null;
        }
        String line = lines.next();
        out().println(PROMPT + line);
        return line;
    }

    private Iterator<String> readRequests() {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isBlank() && !line.strip().startsWith(COMMENT_PREFIX))
                    .iterator();
        } catch (IOException e) {
            throw new UncheckedIOException("Can't read the batch file " + file, e);
        }
    }
}

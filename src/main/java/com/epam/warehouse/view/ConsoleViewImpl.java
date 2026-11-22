package com.epam.warehouse.view;

import com.epam.warehouse.common.AppInfo;
import com.epam.warehouse.controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

/**
 * Reads requests typed in the console.
 */
public final class ConsoleViewImpl extends AbstractView {

    private static final String PROMPT = "> ";

    private final BufferedReader in;

    public ConsoleViewImpl(Controller controller, AppInfo appInfo) {
        this(controller, appInfo, System.in, System.out);
    }

    /**
     * For tests: reads from and writes to the given streams instead of the console.
     */
    public ConsoleViewImpl(Controller controller, AppInfo appInfo, InputStream in, PrintStream out) {
        super(controller, appInfo, out);
        this.in = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
    }

    @Override
    protected String nextLine() {
        out().print(PROMPT);
        out().flush();
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Can't read the console", e);
        }
    }
}

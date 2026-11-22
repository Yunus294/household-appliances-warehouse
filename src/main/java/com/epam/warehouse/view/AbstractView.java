package com.epam.warehouse.view;

import com.epam.warehouse.common.AppInfo;
import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;

import java.io.PrintStream;
import java.util.Objects;

/**
 * The request loop shared by every text view (template method). A subclass only says where the
 * next line comes from.
 */
public abstract class AbstractView implements View {

    private static final String HELP_COMMAND = "help";
    private static final String ERROR_PREFIX = "Error: ";
    private static final String CRASH_MESSAGE =
            "Sorry, something went wrong and the application has to stop. The details are in the log.";

    private final Controller controller;
    private final AppInfo appInfo;
    private final PrintStream out;
    private final CommandLineParser parser = new CommandLineParser();

    protected AbstractView(Controller controller, AppInfo appInfo, PrintStream out) {
        this.controller = Objects.requireNonNull(controller, "controller");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public final void start() {
        printGreeting();
        show(controller.execute(Request.of(HELP_COMMAND)));
        String line;
        while ((line = nextLine()) != null) {
            if (!line.isBlank() && handle(line)) {
                return;
            }
        }
    }

    @Override
    public void crash() {
        out.println(CRASH_MESSAGE);
    }

    /**
     * @return the next line of input, or {@code null} when the input has ended
     */
    protected abstract String nextLine();

    protected final PrintStream out() {
        return out;
    }

    /**
     * @return {@code true} if the application should stop
     */
    private boolean handle(String line) {
        Request request;
        try {
            request = parser.parse(line);
        } catch (InputSyntaxException e) {
            showError(e.getMessage());
            return false;
        }
        Response response = controller.execute(request);
        show(response);
        return response.out();
    }

    private void printGreeting() {
        out.println(appInfo.name() + "  v" + appInfo.version() + "  (" + appInfo.date() + ")");
        out.println("Developer: " + appInfo.developer() + " (" + appInfo.contact() + ")");
        out.println();
    }

    private void show(Response response) {
        if (response.ok()) {
            out.println(response.text());
            out.println();
        } else {
            showError(response.text());
        }
    }

    private void showError(String message) {
        out.println(ERROR_PREFIX + message);
        out.println();
    }
}

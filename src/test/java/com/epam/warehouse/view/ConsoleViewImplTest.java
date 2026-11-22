package com.epam.warehouse.view;

import com.epam.warehouse.common.AppInfo;
import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleViewImplTest {

    static final AppInfo APP = new AppInfo("Test Warehouse", "9.9", "2026-01-01", "Tester", "tester@example.com");

    private final List<Request> received = new ArrayList<>();
    private final Controller controller = request -> {
        received.add(request);
        return switch (request.command()) {
            case "help" -> Response.ok("HELP TEXT");
            case "exit" -> Response.exit("BYE");
            case "fail" -> Response.failure("DATA IS GONE");
            case "find" -> Response.ok("FOUND");
            default -> Response.error("UNKNOWN");
        };
    };

    private String run(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Charset charset = Charset.defaultCharset();
        new ConsoleViewImpl(controller, APP, new ByteArrayInputStream(input.getBytes(charset)),
                new PrintStream(output, true, charset)).start();
        return output.toString(charset);
    }

    private List<String> commands() {
        return received.stream().map(Request::command).toList();
    }

    @Test
    void greetsAndShowsTheMenuFirst() {
        List<String> lines = run("exit\n").lines().toList();

        assertEquals("Test Warehouse  v9.9  (2026-01-01)", lines.get(0));
        assertEquals("Developer: Tester (tester@example.com)", lines.get(1));
        assertEquals("HELP TEXT", lines.get(3));
        assertEquals("help", received.get(0).command());
    }

    @Test
    void sendsParsedRequestsToTheController() {
        run("find laptops ram=16\nexit\n");

        assertEquals(new Request("find", List.of("laptops"), Map.of("ram", "16")), received.get(1));
    }

    @Test
    void stopsOnExit() {
        String output = run("exit\nfind laptops\n");

        assertEquals(List.of("help", "exit"), commands());
        assertTrue(output.contains("BYE"));
    }

    @Test
    void stopsWhenTheInputEnds() {
        run("find laptops\n");

        assertEquals(List.of("help", "find"), commands());
    }

    @Test
    void stopsWhenTheDataFails() {
        String output = run("fail\nfind laptops\n");

        assertEquals(List.of("help", "fail"), commands());
        assertTrue(output.contains("Error: DATA IS GONE"));
    }

    @Test
    void showsErrorsAndGoesOn() {
        String output = run("finde laptops\nfind laptops\nexit\n");

        assertTrue(output.contains("Error: UNKNOWN"));
        assertEquals(List.of("help", "finde", "find", "exit"), commands());
    }

    @Test
    void badInputNeverReachesTheController() {
        String output = run("find laptops name=\"vivo\n\n   \nexit\n");

        assertTrue(output.contains("Error: A closing quote (\") is missing"));
        assertEquals(List.of("help", "exit"), commands());
    }

    @Test
    void crashSaysSorry() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new ConsoleViewImpl(controller, APP, new ByteArrayInputStream(new byte[0]), new PrintStream(output, true)).crash();

        assertTrue(output.toString().startsWith("Sorry, something went wrong"));
    }
}

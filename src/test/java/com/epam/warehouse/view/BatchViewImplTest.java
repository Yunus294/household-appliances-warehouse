package com.epam.warehouse.view;

import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatchViewImplTest {

    private final List<String> received = new ArrayList<>();
    private final Controller controller = request -> {
        received.add(request.command());
        return "exit".equals(request.command()) ? Response.exit("BYE") : Response.ok("ANSWER TO " + request.command());
    };
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private View batch(Path file) {
        return new BatchViewImpl(controller, ConsoleViewImplTest.APP, file, new PrintStream(output, true));
    }

    @Test
    void runsTheRequestsOfTheFile(@TempDir Path folder) throws IOException {
        Path file = Files.writeString(folder.resolve("commands.txt"),
                "# demo\n\nfind laptops\nlist ovens\nexit\ncost all\n");

        batch(file).start();

        assertEquals(List.of("help", "find", "list", "exit"), received);
        String text = output.toString();
        assertTrue(text.contains("> find laptops"));
        assertTrue(text.contains("ANSWER TO list"));
        assertFalse(text.contains("# demo"));
    }

    @Test
    void endsWithTheFile(@TempDir Path folder) throws IOException {
        batch(Files.writeString(folder.resolve("commands.txt"), "find laptops")).start();

        assertEquals(List.of("help", "find"), received);
    }

    @Test
    void missingFileIsAnError(@TempDir Path folder) {
        View view = batch(folder.resolve("missing.txt"));

        assertThrows(UncheckedIOException.class, view::start);
        assertEquals(List.of("help"), received);
    }

    @Test
    void requestsArePassedAsRequests(@TempDir Path folder) throws IOException {
        List<Request> requests = new ArrayList<>();
        Controller recording = request -> {
            requests.add(request);
            return Response.ok("");
        };
        Path file = Files.writeString(folder.resolve("commands.txt"), "find laptops ram=16\n");

        new BatchViewImpl(recording, ConsoleViewImplTest.APP, file, new PrintStream(output, true)).start();

        assertEquals("16", requests.get(1).options().get("ram"));
    }
}

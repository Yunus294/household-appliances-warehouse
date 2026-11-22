package com.epam.warehouse.view;

import com.epam.warehouse.controller.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandLineParserTest {

    private final CommandLineParser parser = new CommandLineParser();

    @Test
    void splitsCommandTargetAndOptions() throws InputSyntaxException {
        assertEquals(new Request("find", List.of("laptops"), Map.of("ram", "16", "price", "500;900")),
                parser.parse("find laptops ram=16 price=500;900"));
    }

    @Test
    void keepsOptionOrder() throws InputSyntaxException {
        assertEquals(List.of("sort", "order"), List.copyOf(parser.parse("find all sort=price order=desc").options().keySet()));
    }

    @Test
    void quotedValueKeepsItsSpaces() throws InputSyntaxException {
        assertEquals(Map.of("name", "MacBook Air"), parser.parse("find laptops name=\"MacBook Air\"").options());
    }

    @Test
    void commandAndKeysIgnoreCaseButValuesDont() throws InputSyntaxException {
        assertEquals(new Request("find", List.of("Laptops"), Map.of("cpu", "Intel")),
                parser.parse("FIND Laptops CPU=Intel"));
    }

    @Test
    void extraSpacesDontMatter() throws InputSyntaxException {
        assertEquals(new Request("list", List.of("ovens"), Map.of()), parser.parse("   list    ovens   "));
    }

    @Test
    void wordWithoutEqualsSignIsAnArgument() throws InputSyntaxException {
        assertEquals(List.of("all", "price<45000"), parser.parse("find all price<45000").arguments());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "find laptops name=\"MacBook        | A closing quote (\") is missing",
            "find laptops price=                | Parameter 'price' has no value",
            "find laptops name=\"\"             | Parameter 'name' has no value",
            "find laptops ram=16 RAM=8          | Parameter 'ram' is given twice",
            "find laptops =16                   | '' is not a parameter name. Write key=value, e.g. price=500;900",
            "find laptops 2ram=16               | '2ram' is not a parameter name. Write key=value, e.g. price=500;900",
            "123 laptops                        | '123' is not a command name",
            "'   '                              | Type a command, or 'help' to see them"})
    void badShapeIsRejectedBeforeTheController(String line, String message) {
        InputSyntaxException e = assertThrows(InputSyntaxException.class, () -> parser.parse(line));
        assertEquals(message, e.getMessage());
    }
}

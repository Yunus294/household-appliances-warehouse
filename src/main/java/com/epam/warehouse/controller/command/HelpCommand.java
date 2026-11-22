package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;
import com.epam.warehouse.controller.argument.Attribute;
import com.epam.warehouse.controller.argument.Attributes;
import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.argument.Targets;
import com.epam.warehouse.controller.format.TextTable;
import com.epam.warehouse.entity.Category;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * {@code help}: the commands, targets and search parameters, built from {@link CommandName} and
 * {@link Attributes}, so the help can't fall behind the code.
 */
public final class HelpCommand implements Command {

    private static final String NEW_LINE = "\n";
    private static final List<String> EXAMPLES = List.of(
            "find laptops ram=16 price=500;1300 sort=price",
            "find all brand=samsung",
            "find laptops name=\"macbook air\"",
            "find all power=0;2500 sort=power order=desc",
            "list refrigerators sort=price order=desc",
            "cost all",
            "cost washing-machines energy=a");

    private final String text = buildText();

    @Override
    public Response execute(Request request) {
        return Response.ok(text);
    }

    private static String buildText() {
        return "Commands:" + NEW_LINE + commands() + NEW_LINE + NEW_LINE
                + "Targets: " + Targets.choices() + NEW_LINE + NEW_LINE
                + "Search parameters:" + NEW_LINE + parameters() + NEW_LINE + NEW_LINE
                + "Text matches any part of the value, ignoring case: cpu=i5" + NEW_LINE
                + "Numbers take one value or a range from;to: price=500;900" + NEW_LINE
                + "Put values with spaces in quotes: name=\"macbook air\"" + NEW_LINE
                + "Sort by any parameter: " + QueryParser.SORT + "=price " + QueryParser.ORDER + "=desc"
                + NEW_LINE + NEW_LINE
                + "Examples:" + NEW_LINE + "  " + String.join(NEW_LINE + "  ", EXAMPLES);
    }

    private static String commands() {
        TextTable table = new TextTable("Command", "What it does");
        for (CommandName name : CommandName.values()) {
            table.addRow(name.usage(), name.description());
        }
        return table.render();
    }

    private static String parameters() {
        TextTable table = new TextTable("Key", "For", "Meaning");
        for (Attribute<?, ?> attribute : Attributes.all()) {
            table.addRow(attribute.key(), Targets.describe(categoriesOf(attribute)), attribute.help());
        }
        return table.render();
    }

    private static Set<Category> categoriesOf(Attribute<?, ?> attribute) {
        Set<Category> categories = EnumSet.noneOf(Category.class);
        for (Category category : Category.values()) {
            if (attribute.appliesTo(category)) {
                categories.add(category);
            }
        }
        return categories;
    }
}

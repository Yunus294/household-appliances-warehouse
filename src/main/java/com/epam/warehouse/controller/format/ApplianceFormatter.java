package com.epam.warehouse.controller.format;

import com.epam.warehouse.controller.argument.Attribute;
import com.epam.warehouse.controller.argument.Attributes;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.service.Stock;
import com.epam.warehouse.service.StockReport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Turns service results into text for the user.
 *
 * <p>The "Details" column lists the category's own properties as {@code key=value}, written the
 * same way as in a search, so any of them can be copied into the next command.
 */
public final class ApplianceFormatter {

    private static final String NEW_LINE = "\n";
    private static final String DETAILS_SEPARATOR = "  ";
    private static final String TOTAL = "Total";

    private final List<Attribute<?, ?>> details = Attributes.all().stream()
            .filter(attribute -> !attribute.isCommon())
            .toList();

    public String appliances(List<Appliance> appliances) {
        TextTable table = new TextTable("ID", "Category", "Name", "Price $", "Qty", "Total $", "Kg", "Details")
                .alignRight(0, 3, 4, 5, 6);
        for (Appliance appliance : appliances) {
            table.addRow(
                    String.valueOf(appliance.getId()),
                    appliance.getCategory().getDisplayName(),
                    appliance.getName(),
                    Numbers.money(appliance.getPrice()),
                    String.valueOf(appliance.getQuantity()),
                    Numbers.money(appliance.getTotalCost()),
                    Numbers.decimal(appliance.getWeight()),
                    details(appliance));
        }
        return "Found " + count(appliances.size()) + ":" + NEW_LINE + table.render();
    }

    public String stock(StockReport report) {
        TextTable table = new TextTable("Category", "Models", "Units", "Value $").alignRight(1, 2, 3);
        report.byCategory().forEach((category, stock) -> addRow(table, category.getDisplayName(), stock));
        if (report.byCategory().size() > 1) {
            addRow(table, TOTAL, report.total());
        }
        return "Stock:" + NEW_LINE + table.render();
    }

    private String details(Appliance appliance) {
        return details.stream()
                .map(attribute -> attribute.format(appliance).map(value -> attribute.key() + "=" + value))
                .flatMap(Optional::stream)
                .collect(Collectors.joining(DETAILS_SEPARATOR));
    }

    private static void addRow(TextTable table, String name, Stock stock) {
        table.addRow(name, String.valueOf(stock.models()), String.valueOf(stock.units()), Numbers.money(stock.value()));
    }

    private static String count(int appliances) {
        return appliances + (appliances == 1 ? " appliance" : " appliances");
    }
}

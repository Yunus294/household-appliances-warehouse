package com.epam.warehouse.controller.argument;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.epam.warehouse.controller.TestRequests.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryParserTest {

    private final QueryParser parser = new QueryParser();
    private final Laptop laptop16 = Appliances.laptop().id(1).ramGb(16).price(700).build();
    private final Laptop laptop8 = Appliances.laptop().id(2).ramGb(8).price(500).build();
    private final Oven oven = Appliances.oven().id(3).price(600).build();

    private Query parse(Request request) throws InvalidRequestException {
        return parser.parse(request, QuerySyntax.FILTERS_AND_SORTING);
    }

    private String errorOf(Request request, QuerySyntax syntax) {
        return assertThrows(InvalidRequestException.class, () -> parser.parse(request, syntax)).getMessage();
    }

    @Test
    void targetIsACategoryOrAll() throws InvalidRequestException {
        assertEquals(Set.of(Category.LAPTOP), parse(request("find", "laptops")).categories());
        assertEquals(Set.of(Category.WASHING_MACHINE), parse(request("find", "Washing-Machine")).categories());
        assertEquals(EnumSet.allOf(Category.class), parse(request("find", "all")).categories());
    }

    @Test
    void filtersBecomeCriteria() throws InvalidRequestException {
        Query query = parse(request("find", "all", Map.of("ram", "16", "price", "600;800")));

        assertTrue(query.criteria().test(laptop16));
        assertFalse(query.criteria().test(laptop8));
        assertFalse(query.criteria().test(oven));
    }

    @Test
    void defaultOrderIsById() throws InvalidRequestException {
        List<Appliance> sorted = sort(List.of(oven, laptop8, laptop16), parse(request("find", "all")));

        assertEquals(List.of(laptop16, laptop8, oven), sorted);
    }

    @Test
    void sortsByAnyKeyInEitherDirection() throws InvalidRequestException {
        Query cheapFirst = parse(request("find", "all", Map.of("sort", "price")));
        Query expensiveFirst = parse(request("find", "all", Map.of("sort", "PRICE", "order", "DESC")));

        assertEquals(List.of(laptop8, oven, laptop16), sort(List.of(oven, laptop8, laptop16), cheapFirst));
        assertEquals(List.of(laptop16, oven, laptop8), sort(List.of(oven, laptop8, laptop16), expensiveFirst));
    }

    @Test
    void missingTargetIsExplained() {
        assertEquals("Say where to look: laptops, ovens, refrigerators, washing-machines or all. "
                + "For example: find laptops", errorOf(Request.of("find"), QuerySyntax.FILTERS_AND_SORTING));
    }

    @Test
    void secondWordIsNotAccepted() {
        assertEquals("Unexpected 'price<45000'. Parameters are written as key=value, e.g. price=500;900",
                errorOf(new Request("find", List.of("all", "price<45000"), Map.of()), QuerySyntax.FILTERS_AND_SORTING));
    }

    @Test
    void unknownTargetListsTheChoices() {
        assertEquals("Unknown target 'toasters'. Use laptops, ovens, refrigerators, washing-machines or all",
                errorOf(request("find", "toasters"), QuerySyntax.FILTERS_AND_SORTING));
    }

    @Test
    void keyMustApplyToTheTarget() {
        assertEquals("'nofrost' doesn't apply to laptops",
                errorOf(request("find", "laptops", Map.of("nofrost", "yes")), QuerySyntax.FILTERS_AND_SORTING));
        assertEquals("Unknown parameter 'color'. Type 'help' to see the parameters",
                errorOf(request("find", "all", Map.of("color", "red")), QuerySyntax.FILTERS_AND_SORTING));
    }

    @Test
    void listTakesNoFiltersAndCostDoesntSort() {
        assertEquals("'list' takes no filters (price). Use 'find' to filter",
                errorOf(request("list", "all", Map.of("price", "1;2")), QuerySyntax.SORTING_ONLY));
        assertEquals("'cost' doesn't sort. Use 'find' or 'list' to sort",
                errorOf(request("cost", "all", Map.of("order", "desc")), QuerySyntax.FILTERS_ONLY));
    }

    @Test
    void orderIsAscOrDesc() {
        assertEquals("'order' must be asc or desc, but was 'up'",
                errorOf(request("find", "all", Map.of("order", "up")), QuerySyntax.FILTERS_AND_SORTING));
    }

    private static List<Appliance> sort(List<Appliance> appliances, Query query) {
        return appliances.stream().sorted(query.order()).toList();
    }
}

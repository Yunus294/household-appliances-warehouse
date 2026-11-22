package com.epam.warehouse.controller;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.dao.FileStorage;
import com.epam.warehouse.dao.format.CsvFormat;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.service.ApplianceService;
import com.epam.warehouse.service.ApplianceServiceImpl;
import com.epam.warehouse.service.ServiceException;
import com.epam.warehouse.service.StockReport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.epam.warehouse.controller.TestRequests.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * The controller over the real service and DAO layers, reading the test CSV files.
 */
class ControllerImplTest {

    private static Controller controller;

    @BeforeAll
    static void init() {
        controller = new ControllerImpl(new ApplianceServiceImpl(new FileStorage(new CsvFormat())
                .file(Category.LAPTOP, "laptops-test.csv")
                .file(Category.OVEN, "ovens-test.csv")
                .file(Category.REFRIGERATOR, "refrigerators-test.csv")
                .file(Category.WASHING_MACHINE, "washing-machines-test.csv")
                .build()));
    }

    static Stream<Arguments> searches() {
        return Stream.of(
                arguments(request("find", "laptops"), 4),
                arguments(request("find", "laptop"), 4),
                arguments(request("list", "all"), 11),
                arguments(request("find", "laptops", Map.of("cpu", "intel")), 2),
                arguments(request("find", "laptops", Map.of("price", "500;1000")), 3),
                arguments(request("find", "laptops", Map.of("os", "macos", "ram", "8")), 1),
                arguments(request("find", "all", Map.of("price", "300;600")), 7),
                arguments(request("find", "all", Map.of("brand", "SAMSUNG")), 3),
                arguments(request("find", "all", Map.of("power", "0;2500")), 5),
                arguments(request("find", "all", Map.of("os", "linux")), 1),
                arguments(request("find", "washing-machines", Map.of("energy", "A")), 1),
                arguments(request("find", "refrigerators", Map.of("nofrost", "yes")), 1),
                arguments(request("find", "ovens", Map.of("quantity", "0")), 1));
    }

    @ParameterizedTest
    @MethodSource("searches")
    void findsMatchingAppliances(Request request, int expected) {
        Response response = controller.execute(request);

        assertTrue(response.ok(), response.text());
        assertTrue(response.text().startsWith("Found " + expected + " appliance"), response.text());
    }

    static Stream<Request> wrongRequests() {
        return Stream.of(
                Request.of("finde"),
                Request.of("find"),
                new Request("find", List.of("all", "price<45000"), Map.of()),
                request("find", "laptopz"),
                request("find", "all", Map.of("price", "45000;0")),
                request("find", "all", Map.of("price", "10000;abc")),
                request("find", "all", Map.of("price", ";20000")),
                request("find", "all", Map.of("price", "1;2;3")),
                request("find", "all", Map.of("price", "-5")),
                request("find", "laptops", Map.of("ram", "16.5")),
                request("find", "laptops", Map.of("os", "solaris")),
                request("find", "ovens", Map.of("os", "linux")),
                request("find", "all", Map.of("color", "red")),
                request("find", "refrigerators", Map.of("nofrost", "maybe")),
                request("find", "all", Map.of("sort", "color")),
                request("find", "laptops", Map.of("sort", "volume")),
                request("find", "all", Map.of("order", "up")),
                request("list", "laptops", Map.of("price", "500;900")),
                request("cost", "laptops", Map.of("sort", "price")));
    }

    @ParameterizedTest
    @MethodSource("wrongRequests")
    void wrongRequestIsRejectedButTheAppGoesOn(Request request) {
        Response response = controller.execute(request);

        assertFalse(response.ok(), response.text());
        assertFalse(response.out());
    }

    @Test
    void explainsWhatIsWrong() {
        assertEquals("'price' must be a whole number, but was 'abc'",
                controller.execute(request("find", "all", Map.of("price", "10000;abc"))).text());
        assertEquals("'os' doesn't apply to ovens",
                controller.execute(request("find", "ovens", Map.of("os", "linux"))).text());
        assertEquals("Unknown command 'finde'. Type 'help' to see the commands",
                controller.execute(Request.of("finde")).text());
    }

    @Test
    void saysWhenNothingIsFound() {
        Response response = controller.execute(request("find", "laptops", Map.of("price", "5000;9000")));

        assertTrue(response.ok());
        assertEquals("No appliances found.", response.text());
    }

    @Test
    void sortsByTheGivenKeyAndOrder() {
        String text = controller.execute(request("find", "laptops", Map.of("sort", "price", "order", "desc"))).text();

        assertTrue(text.indexOf("Dell XPS 13") < text.indexOf("Apple MacBook Air 13"));
        assertTrue(text.indexOf("Apple MacBook Air 13") < text.indexOf("ASUS Vivobook 15"));
        assertTrue(text.indexOf("ASUS Vivobook 15") < text.indexOf("Lenovo IdeaPad Slim 3"));
    }

    @Test
    void appliancesWithoutTheSortKeyGoLast() {
        List<String> lines = controller.execute(request("list", "all", Map.of("sort", "ram", "order", "desc")))
                .text().lines().toList();

        assertTrue(lines.get(3).contains("ASUS Vivobook 15"), lines.get(3));
        assertTrue(lines.get(lines.size() - 1).contains("Washing machine"), lines.get(lines.size() - 1));
    }

    @Test
    void costSumsTheStock() {
        Response response = controller.execute(request("cost", "all"));

        assertTrue(response.ok());
        String totals = "Total" + " ".repeat(16) + "11" + " ".repeat(5) + "79" + " ".repeat(3) + "47,300";
        assertTrue(response.text().lines().anyMatch(totals::equals), response.text());
    }

    @Test
    void helpListsCommandsAndParameters() {
        Response response = controller.execute(Request.of("help"));

        assertTrue(response.ok());
        assertTrue(response.text().contains("find <target> [key=value ...] [sort=key] [order=asc|desc]"));
        assertTrue(response.text().contains("nofrost"));
    }

    @Test
    void exitStopsTheApplication() {
        Response response = controller.execute(Request.of("exit"));

        assertTrue(response.ok());
        assertTrue(response.out());
    }

    @Test
    void unavailableServiceStopsTheApplication() {
        Controller broken = new ControllerImpl(new FailingService(new ServiceException("laptops.csv is gone")));

        Response response = broken.execute(request("find", "laptops"));

        assertFalse(response.ok());
        assertTrue(response.out());
        assertEquals("laptops.csv is gone", response.text());
    }

    @Test
    void unexpectedErrorIsReportedAndTheAppGoesOn() {
        Controller buggy = new ControllerImpl(new FailingService(new IllegalStateException("bug")));

        Response response = buggy.execute(request("cost", "all"));

        assertFalse(response.ok());
        assertFalse(response.out());
    }

    /**
     * A service that always fails with the given exception.
     */
    private record FailingService(Exception failure) implements ApplianceService {

        @Override
        public List<Appliance> find(Collection<Category> categories, SearchCriteria<Appliance> criteria,
                                    Comparator<Appliance> order) throws ServiceException {
            throw rethrow();
        }

        @Override
        public StockReport stockReport(Collection<Category> categories, SearchCriteria<Appliance> criteria)
                throws ServiceException {
            throw rethrow();
        }

        private ServiceException rethrow() {
            if (failure instanceof RuntimeException runtime) {
                throw runtime;
            }
            return (ServiceException) failure;
        }
    }
}

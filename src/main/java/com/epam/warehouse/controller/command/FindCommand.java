package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.argument.Query;
import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.argument.QuerySyntax;
import com.epam.warehouse.controller.format.ApplianceFormatter;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.service.ApplianceService;
import com.epam.warehouse.service.ServiceException;

import java.util.List;
import java.util.Objects;

/**
 * {@code find <target> [key=value ...] [sort=key] [order=asc|desc]}
 */
public class FindCommand extends QueryCommand {

    private static final String NOTHING_FOUND = "No appliances found.";

    private final ApplianceService service;
    private final ApplianceFormatter formatter;

    public FindCommand(ApplianceService service, QueryParser parser, ApplianceFormatter formatter) {
        this(service, parser, formatter, QuerySyntax.FILTERS_AND_SORTING);
    }

    protected FindCommand(ApplianceService service, QueryParser parser, ApplianceFormatter formatter,
                          QuerySyntax syntax) {
        super(parser, syntax);
        this.service = Objects.requireNonNull(service, "service");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    @Override
    protected final String run(Query query) throws ServiceException {
        List<Appliance> found = service.find(query.categories(), query.criteria(), query.order());
        return found.isEmpty() ? NOTHING_FOUND : formatter.appliances(found);
    }
}

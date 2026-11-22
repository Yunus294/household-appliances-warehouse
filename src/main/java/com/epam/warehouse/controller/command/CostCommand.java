package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.argument.Query;
import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.argument.QuerySyntax;
import com.epam.warehouse.controller.format.ApplianceFormatter;
import com.epam.warehouse.service.ApplianceService;
import com.epam.warehouse.service.ServiceException;

import java.util.Objects;

/**
 * {@code cost <target> [key=value ...]}: models, units and stock value per category.
 */
public final class CostCommand extends QueryCommand {

    private final ApplianceService service;
    private final ApplianceFormatter formatter;

    public CostCommand(ApplianceService service, QueryParser parser, ApplianceFormatter formatter) {
        super(parser, QuerySyntax.FILTERS_ONLY);
        this.service = Objects.requireNonNull(service, "service");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    @Override
    protected String run(Query query) throws ServiceException {
        return formatter.stock(service.stockReport(query.categories(), query.criteria()));
    }
}

package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.argument.QuerySyntax;
import com.epam.warehouse.controller.format.ApplianceFormatter;
import com.epam.warehouse.service.ApplianceService;

/**
 * {@code list <target> [sort=key] [order=asc|desc]}: a search without filters.
 */
public final class ListCommand extends FindCommand {

    public ListCommand(ApplianceService service, QueryParser parser, ApplianceFormatter formatter) {
        super(service, parser, formatter, QuerySyntax.SORTING_ONLY);
    }
}

package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;
import com.epam.warehouse.controller.argument.InvalidRequestException;
import com.epam.warehouse.controller.argument.Query;
import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.argument.QuerySyntax;
import com.epam.warehouse.service.ServiceException;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A command that asks the service about part of the stock (template method).
 *
 * <p>The steps are always the same: parse the request, ask the service, format the answer. The
 * main scenario is in {@link #run}; the two ways out are handled here, apart from it:
 * <ul>
 *     <li>a wrong request — the user sees why and can try again;</li>
 *     <li>the service failed — the cause is logged and the application stops.</li>
 * </ul>
 */
public abstract class QueryCommand implements Command {

    private static final Logger LOG = Logger.getLogger(QueryCommand.class.getName());

    private final QueryParser parser;
    private final QuerySyntax syntax;

    protected QueryCommand(QueryParser parser, QuerySyntax syntax) {
        this.parser = Objects.requireNonNull(parser, "parser");
        this.syntax = Objects.requireNonNull(syntax, "syntax");
    }

    @Override
    public final Response execute(Request request) {
        Query query;
        try {
            query = parser.parse(request, syntax);
        } catch (InvalidRequestException e) {
            return Response.error(e.getMessage());
        }
        try {
            return Response.ok(run(query));
        } catch (ServiceException e) {
            LOG.log(Level.SEVERE, "Service failed on " + request, e);
            return Response.failure(e.getMessage());
        }
    }

    /**
     * @return the answer for the user
     */
    protected abstract String run(Query query) throws ServiceException;
}

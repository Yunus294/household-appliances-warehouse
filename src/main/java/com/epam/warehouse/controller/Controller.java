package com.epam.warehouse.controller;

/**
 * Takes a user request, runs it and returns the answer. Doesn't read input or print output:
 * that is the view's job.
 */
@FunctionalInterface
public interface Controller {

    Response execute(Request request);
}

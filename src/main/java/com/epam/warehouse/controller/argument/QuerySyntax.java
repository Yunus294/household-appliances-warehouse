package com.epam.warehouse.controller.argument;

/**
 * Which options a command accepts besides its target.
 */
public enum QuerySyntax {

    /**
     * {@code find}: filters and sorting.
     */
    FILTERS_AND_SORTING(true, true),
    /**
     * {@code list}: sorting only.
     */
    SORTING_ONLY(false, true),
    /**
     * {@code cost}: filters only.
     */
    FILTERS_ONLY(true, false);

    private final boolean filters;
    private final boolean sorting;

    QuerySyntax(boolean filters, boolean sorting) {
        this.filters = filters;
        this.sorting = sorting;
    }

    public boolean allowsFilters() {
        return filters;
    }

    public boolean allowsSorting() {
        return sorting;
    }
}

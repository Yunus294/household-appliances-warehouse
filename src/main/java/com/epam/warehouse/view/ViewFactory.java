package com.epam.warehouse.view;

import com.epam.warehouse.common.InstanceHolder;

/**
 * Entry point of the view layer: the view chosen at startup. {@link #init} works once; later calls
 * are ignored.
 */
public final class ViewFactory {

    private static final InstanceHolder<View> VIEW = new InstanceHolder<>("View");

    private ViewFactory() {
    }

    public static void init(View view) {
        VIEW.init(view);
    }

    public static View getInstance() {
        return VIEW.get();
    }
}

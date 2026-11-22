package com.epam.warehouse.controller;

import com.epam.warehouse.common.InstanceHolder;

/**
 * Entry point of the controller layer: the controller chosen at startup. {@link #init} works once;
 * later calls are ignored.
 */
public final class ControllerFactory {

    private static final InstanceHolder<Controller> CONTROLLER = new InstanceHolder<>("Controller");

    private ControllerFactory() {
    }

    public static void init(Controller controller) {
        CONTROLLER.init(controller);
    }

    public static Controller getInstance() {
        return CONTROLLER.get();
    }
}

package com.epam.warehouse.service;

import com.epam.warehouse.common.InstanceHolder;

/**
 * Entry point of the service layer: the service chosen at startup. {@link #init} works once;
 * later calls are ignored.
 */
public final class ServiceFactory {

    private static final InstanceHolder<ApplianceService> SERVICE = new InstanceHolder<>("Appliance service");

    private ServiceFactory() {
    }

    public static void init(ApplianceService service) {
        SERVICE.init(service);
    }

    public static ApplianceService getInstance() {
        return SERVICE.get();
    }
}

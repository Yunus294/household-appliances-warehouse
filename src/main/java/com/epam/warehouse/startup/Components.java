package com.epam.warehouse.startup;

import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.dao.DaoRegistry;
import com.epam.warehouse.service.ApplianceService;
import com.epam.warehouse.view.View;

/**
 * Every layer, built and wired together.
 */
record Components(DaoRegistry daos, ApplianceService service, Controller controller, View view) {
}

package com.epam.warehouse.startup;

import com.epam.warehouse.common.AppInfo;
import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.controller.ControllerFactory;
import com.epam.warehouse.dao.DaoFactory;
import com.epam.warehouse.dao.DaoRegistry;
import com.epam.warehouse.dao.FileStorage;
import com.epam.warehouse.dao.format.RecordFormat;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.service.ApplianceService;
import com.epam.warehouse.service.ServiceFactory;
import com.epam.warehouse.view.View;
import com.epam.warehouse.view.ViewFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Builds the application from a {@code .properties} file (see {@code app.properties}).
 *
 * <p>Layers are built bottom-up, each getting the one below through its constructor:
 * DAO → service → controller → view. The classes of the service, controller, view and data
 * format are read from the settings and created by reflection, so switching an implementation
 * doesn't need a code change.
 */
public final class PropertiesConfigImpl implements Config {

    static final String APP_NAME = "app.name";
    static final String APP_VERSION = "app.version";
    static final String APP_DATE = "app.date";
    static final String DEVELOPER_NAME = "developer.name";
    static final String DEVELOPER_CONTACT = "developer.contact";
    static final String VIEW = "view";
    static final String BATCH_VIEW = "view.batch";
    static final String CONTROLLER = "controller";
    static final String SERVICE = "service";
    static final String DAO_FORMAT = "dao.format";
    /**
     * A file shared by every category without a file of its own.
     */
    static final String DAO_FILE = "dao.file";
    /**
     * Prefix of a category's own file: {@code dao.file.laptop = laptops.csv}.
     */
    static final String DAO_FILE_PREFIX = DAO_FILE + ".";

    private final LaunchOptions options;
    private final ComponentLoader loader = new ComponentLoader();

    public PropertiesConfigImpl(LaunchOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    @Override
    public void init() {
        Components components = assemble();
        DaoFactory.init(components.daos());
        ServiceFactory.init(components.service());
        ControllerFactory.init(components.controller());
        ViewFactory.init(components.view());
    }

    /**
     * Builds every layer without touching the factories, so tests can check the result.
     */
    Components assemble() {
        Settings settings = Settings.load(options.configName());
        AppInfo appInfo = new AppInfo(
                settings.required(APP_NAME),
                settings.required(APP_VERSION),
                settings.required(APP_DATE),
                settings.required(DEVELOPER_NAME),
                settings.required(DEVELOPER_CONTACT));
        DaoRegistry daos = daoRegistry(settings);
        ApplianceService service = loader.create(settings.required(SERVICE), ApplianceService.class, daos);
        Controller controller = loader.create(settings.required(CONTROLLER), Controller.class, service);
        View view = view(settings, controller, appInfo);
        return new Components(daos, service, controller, view);
    }

    private DaoRegistry daoRegistry(Settings settings) {
        RecordFormat format = loader.create(settings.required(DAO_FORMAT), RecordFormat.class);
        FileStorage storage = new FileStorage(format);
        settings.optional(DAO_FILE).ifPresent(storage::sharedFile);
        for (Category category : Category.values()) {
            settings.optional(DAO_FILE_PREFIX + category.getKey()).ifPresent(file -> storage.file(category, file));
        }
        try {
            return storage.build();
        } catch (IllegalStateException e) {
            throw new ConfigException(e.getMessage() + " in " + settings.fileName(), e);
        }
    }

    private View view(Settings settings, Controller controller, AppInfo appInfo) {
        Optional<Path> batchFile = options.batchFile();
        if (batchFile.isPresent()) {
            return loader.create(settings.required(BATCH_VIEW), View.class, controller, appInfo, batchFile.get());
        }
        return loader.create(settings.required(VIEW), View.class, controller, appInfo);
    }
}

package com.epam.warehouse.startup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Creates objects by class name (Reflection API), so the implementation of each layer is chosen in
 * the settings file, not in code.
 *
 * <p>The dependencies are passed to the first public constructor that accepts them, e.g.
 * {@code ControllerImpl(ApplianceService)}.
 */
final class ComponentLoader {

    /**
     * @param className    full class name, e.g. {@code com.epam.warehouse.view.ConsoleViewImpl}
     * @param type         what the object must be
     * @param dependencies constructor arguments
     * @throws ConfigException if the class is missing, of the wrong type, has no fitting
     *                         constructor or its constructor fails
     */
    <T> T create(String className, Class<T> type, Object... dependencies) {
        Class<?> implementation = load(className);
        if (!type.isAssignableFrom(implementation)) {
            throw new ConfigException(className + " is not a " + type.getSimpleName());
        }
        Constructor<?> constructor = constructorFor(implementation, dependencies);
        try {
            return type.cast(constructor.newInstance(dependencies));
        } catch (InvocationTargetException e) {
            throw new ConfigException("Can't create " + className + ": " + e.getCause().getMessage(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new ConfigException("Can't create " + className, e);
        }
    }

    private static Class<?> load(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ConfigException("Class " + className + " not found", e);
        }
    }

    private static Constructor<?> constructorFor(Class<?> implementation, Object[] dependencies) {
        for (Constructor<?> constructor : implementation.getConstructors()) {
            if (accepts(constructor.getParameterTypes(), dependencies)) {
                return constructor;
            }
        }
        String types = Arrays.stream(dependencies)
                .map(dependency -> dependency.getClass().getSimpleName())
                .collect(Collectors.joining(", "));
        throw new ConfigException(implementation.getName() + " has no public constructor (" + types + ")");
    }

    private static boolean accepts(Class<?>[] parameters, Object[] arguments) {
        if (parameters.length != arguments.length) {
            return false;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].isInstance(arguments[i])) {
                return false;
            }
        }
        return true;
    }
}

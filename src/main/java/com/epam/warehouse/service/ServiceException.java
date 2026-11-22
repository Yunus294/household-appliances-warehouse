package com.epam.warehouse.service;

/**
 * The service can't do its job because the data behind it is unavailable.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

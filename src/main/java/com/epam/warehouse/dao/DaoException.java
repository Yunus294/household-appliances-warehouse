package com.epam.warehouse.dao;

/**
 * The data can't be read: a file is missing, unreadable or has a broken record.
 */
public class DaoException extends Exception {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.epam.warehouse.dao.format;

/**
 * A data file breaks its format: a missing column, a value of the wrong kind and so on.
 */
public class RecordFormatException extends RuntimeException {

    public RecordFormatException(String message) {
        super(message);
    }
}

package com.epam.warehouse.dao.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * A way of writing records in a text file. Swapping the format doesn't touch the rest of the DAO
 * layer: everything after this step works with {@link DataRow}s.
 */
public interface RecordFormat {

    /**
     * @return every record of the text, in order
     * @throws IOException           if the text can't be read
     * @throws RecordFormatException if the text breaks the format
     */
    List<DataRow> read(BufferedReader reader) throws IOException;
}

package com.epam.warehouse.dao;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.dao.format.RecordFormat;
import com.epam.warehouse.dao.format.RecordFormatException;
import com.epam.warehouse.dao.mapper.RecordMapper;
import com.epam.warehouse.entity.Appliance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Appliances stored in a text file.
 *
 * <p>The file is looked up on the classpath first ({@code src/main/resources}), then on disk, so a
 * file outside the jar can be used too. Reading is split in two steps that can be swapped
 * independently: a {@link RecordFormat} turns the text into rows (CSV, key-value text...), and a
 * {@link RecordMapper} turns each row into an appliance.
 *
 * @param <A> the appliance type
 */
public final class FileSource<A extends Appliance> implements Source<A> {

    private final String location;
    private final RecordFormat format;
    private final RecordMapper<A> mapper;
    private final Predicate<DataRow> rowFilter;

    public FileSource(String location, RecordFormat format, RecordMapper<A> mapper) {
        this(location, format, mapper, row -> true);
    }

    /**
     * @param rowFilter which rows belong to this source; used when one file holds every category
     */
    public FileSource(String location, RecordFormat format, RecordMapper<A> mapper,
                      Predicate<DataRow> rowFilter) {
        this.location = Objects.requireNonNull(location, "location");
        this.format = Objects.requireNonNull(format, "format");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.rowFilter = Objects.requireNonNull(rowFilter, "rowFilter");
    }

    @Override
    public String name() {
        return location;
    }

    @Override
    public List<A> readAll() throws DaoException {
        try (BufferedReader reader = open()) {
            List<A> appliances = new ArrayList<>();
            for (DataRow row : format.read(reader)) {
                if (rowFilter.test(row)) {
                    appliances.add(toAppliance(row));
                }
            }
            return appliances;
        } catch (IOException | UncheckedIOException e) {
            throw new DaoException("Can't read " + location + ": " + e.getMessage(), e);
        } catch (RecordFormatException e) {
            throw new DaoException(location + ", " + e.getMessage(), e);
        }
    }

    private BufferedReader open() throws IOException, DaoException {
        InputStream resource = FileSource.class.getClassLoader().getResourceAsStream(location);
        if (resource != null) {
            return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
        }
        Path path = Path.of(location);
        if (Files.isRegularFile(path)) {
            return Files.newBufferedReader(path, StandardCharsets.UTF_8);
        }
        throw new DaoException("Data file not found: " + location);
    }

    private A toAppliance(DataRow row) throws DaoException {
        try {
            return mapper.map(row);
        } catch (RecordFormatException | IllegalArgumentException | IllegalStateException e) {
            throw new DaoException(location + ", line " + row.lineNumber() + ": " + e.getMessage(), e);
        }
    }
}

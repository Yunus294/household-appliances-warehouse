# Household Appliances Warehouse

A console application for searching the stock of a household appliances warehouse: laptops,
ovens, refrigerators and washing machines. The stock is read from text files; the user types
commands such as `find laptops ram=16 price=500;1300 sort=price` and gets a table of matching
appliances.

EPAM University Java course project (SE122_FP_V3). Plain Java 17 and JUnit 5, no frameworks.
The design and the progress log are in [report.md](report.md).

## Build and run

You need JDK 17 or newer. Maven is not required: the Maven wrapper downloads it on the first run.

```sh
./mvnw package                                      # compile, run the tests, build the jar
java -jar target/household-appliances-warehouse.jar
```

On Windows use `mvnw.cmd package`.

Launch options:

| Option | What it does |
|---|---|
| `--config=<name>` | read `<name>.properties` instead of `app.properties`, e.g. `--config=single-file` |
| `--batch=<file>` | run the commands from a text file instead of the console, e.g. `--batch=examples/demo.txt` |

## Commands

```
find <target> [key=value ...] [sort=key] [order=asc|desc]   search the stock
list <target> [sort=key] [order=asc|desc]                   everything in the target, sorted
cost <target> [key=value ...]                               units in stock and what they are worth
help                                                        show the help
exit                                                        quit
```

`<target>` is `laptops`, `ovens`, `refrigerators`, `washing-machines` or `all`.

- Text matches any part of the value, ignoring case: `cpu=i5`
- Numbers take one value or a range `from;to`: `price=500;900`
- Values with spaces go in quotes: `name="macbook air"`
- `help` lists every search key and the categories it applies to.

Examples:

```
find laptops ram=16 price=500;1300 sort=price
find all brand=samsung
find all power=0;2500 sort=power order=desc
list refrigerators sort=price order=desc
cost all
cost washing-machines energy=a
```

## Configuration

`src/main/resources/app.properties` says what the application shows on startup, which class
implements each layer (created by reflection) and where the data is:

```properties
dao.format = com.epam.warehouse.dao.format.CsvFormat
dao.file.laptop = laptops.csv
dao.file.oven = ovens.csv
dao.file.refrigerator = refrigerators.csv
dao.file.washing-machine = washing-machines.csv
```

`single-file.properties` keeps the whole stock in one file of `key: value` records
(`warehouse.txt`) instead. Errors are written to `warehouse.log` in the working directory.

## Project layout

```
src/main/java/com/epam/warehouse/
├── entity/       Appliance and its four categories (immutable, built with builders)
├── criteria/     SearchCriteria, Parameter, Range: search conditions passed between layers
├── dao/          reading the files: formats, row-to-entity mappers, DAO registry
├── service/      searching across categories, sorting, stock totals
├── controller/   commands, request parsing and validation, output formatting
├── view/         console and batch views, client-side input check
├── startup/      Main, settings, building the layers
└── common/       InstanceHolder (shared by the layer factories), AppInfo
src/main/resources/   settings and data files
src/test/             JUnit 5 tests for every layer and their data files
examples/demo.txt     a demo session for --batch
```

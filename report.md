# Progress report: Household Appliances Warehouse

EPAM University, Java course project SE122_FP_V3.
Developer: Yunusbek Ismoilov.

## 1. Task

The general task is a **Warehouse Search System**: a console Java application that reads a
warehouse's stock from prepared text files and lets the user search it by any combination of
parameters. My subject area is a **household appliances warehouse**.

Requirements from the assignment:

- on startup, show the application name, version and date, the developer and the command menu;
- wait for a command, run it, print the result, wait for the next one; one command quits;
- read the stock from a prepared text file;
- search goods by different parameters and show the matches with their details
  (ID, name, category, price, quantity...); say so when nothing is found;
- list all goods sorted in the chosen order;
- handle errors and show clear messages.

Mandatory: a layered architecture, unit tests, the project compiles and runs, no third-party
frameworks.

## 2. Work plan

The course takes 11 weeks with checkpoints at 5 %, 35 % and 70 % of the work.

| # | Stage | Planned | Done |
|---|---|---|---|
| 1 | Clarify the task, write this plan, create the repository and `report.md` | week 1 | ✅ |
| 2 | Analysis: basic, additional and advanced functionality (section 3) | week 1 | ✅ |
| 3 | Use cases (section 4) | week 1 | ✅ |
| 4 | Find solutions: choose the architecture (section 5) | week 2 | ✅ |
| 5.1 | Entity layer + tests | week 3 | ✅ |
| 5.2 | Search criteria and DAO layer (CSV) + tests | weeks 3–4 | ✅ |
| 5.3 | Service layer + tests | week 5 | ✅ |
| 5.4 | Controller layer + tests | weeks 5–6 | ✅ |
| 5.5 | View layer + tests | week 7 | ✅ |
| 5.6 | Startup layer + tests | week 7 | ✅ |
| 5.7 | "Ideas worth implementing" (section 8) | weeks 8–9 | ✅ |
| 6 | Wrap-up: compare this report with the plan, write the conclusions (section 10) | week 10 | |
| 7 | Presentation to the mentor | week 11 | |

## 3. Analysis

**Subject area.** The warehouse stocks appliance models. Every model has a catalog number,
brand, model name, unit price, units in stock and unit weight. Each category adds its own
properties: a laptop has an operating system, processor, memory, disk, screen and battery; an
oven has a power rating, volume and top temperature; a refrigerator has a power rating, energy
class, capacity, freezer volume and frost-free flag; a washing machine has a power rating,
energy class, load and spin speed. Ovens, refrigerators and washing machines all run on mains
power, and refrigerators and washing machines both carry an EU energy label, so some searches
make sense across categories ("everything up to 2.5 kW").

**Basic functionality**

- search one category or all of them by any parameter;
- show each match with its details: ID, category, name, price, quantity, stock value, weight and
  the category's own properties;
- say "No appliances found." when nothing matches;
- list everything in a category, or the whole stock, sorted;
- clear messages for wrong input; `exit` quits.

**Additional functionality**

- numeric parameters take one value or a range (`price=500;900`), text parameters match any
  part ignoring case (`cpu=i5`);
- sort by any parameter, ascending or descending;
- `cost`: models, units and stock value per category, with a total;
- searches across categories by shared properties (`power`, `energy`);
- `help` is generated from the code, so it always lists the real parameters.

**Advanced functionality**

- the data format and file layout are chosen in the settings: a CSV file per category, or one
  text file of `key: value` records for the whole stock;
- batch mode: run commands from a text file;
- the implementation of each layer is chosen in the settings and created by reflection.

Possible next steps (not planned for this course): changing the stock (add, sell, restock) and
saving it, a low-stock alert, export of search results, a graphical view.

## 4. Use cases

The user is a warehouse employee.

| Use case | The user... | The application... |
|---|---|---|
| Start | runs the jar | shows the name, version, date, developer and the menu |
| Find goods | types `find laptops ram=16 price=500;1300` | prints a table of matching laptops, or "No appliances found." |
| Find across categories | types `find all brand=samsung` | searches every category |
| See the whole stock sorted | types `list all sort=price order=desc` | prints everything, most expensive first |
| Know the stock value | types `cost all` or `cost ovens power=0;3000` | prints models, units and value per category and in total |
| Get help | types `help` | prints the commands, targets, every search key and examples |
| Make a mistake | types `find ovens os=linux` or `find all price=900;100` | explains what is wrong; the session goes on |
| Run a prepared session | starts with `--batch=examples/demo.txt` | runs each line and prints it with its answer |
| Quit | types `exit` | says goodbye and stops |
| Data is broken | (a data file is missing or has a bad record) | names the file, line and problem, logs the details and stops |

## 5. Architecture

### 5.1 Layers

```
startup     Main → PropertiesConfigImpl → ComponentLoader (reflection)      reads app.properties
   │ builds every layer and passes each one the layer below
view        ConsoleViewImpl / BatchViewImpl → CommandLineParser             input and output only
   │ Request ↓                         ↑ Response
controller  ControllerImpl → CommandProvider → Find/List/Cost/Help/Exit/WrongCommand
            QueryParser + Attributes: request → Query;  ApplianceFormatter: result → text
   │ categories + SearchCriteria + Comparator ↓    ↑ List<Appliance>, StockReport
service     ApplianceServiceImpl: combines categories, sorts, totals the stock
   │ SearchCriteria ↓                  ↑ List<A>
dao         DaoRegistry → ApplianceDaoImpl → FileSource → RecordFormat → RecordMapper
   │
files       laptops.csv, ovens.csv, refrigerators.csv, washing-machines.csv  (or warehouse.txt)

shared data: entity (Appliance + 4 categories), criteria (SearchCriteria, Parameter, Range),
             common (InstanceHolder, AppInfo)
```

Rules the code follows:

- a layer talks only to the layer right below it, through that layer's interface;
- the lower layer is passed in the constructor (`ControllerImpl(ApplianceService)`), so every
  layer can be tested with a stand-in for the layer below;
- no layer creates an implementation class of another layer; only `startup` does, as the place
  where the application is put together. Entities, criteria and the controller's `Request` and
  `Response` records are plain data and are created where needed;
- each layer has a factory (`DaoFactory`, `ServiceFactory`, `ControllerFactory`, `ViewFactory`)
  holding the instance chosen at startup; `init` works once.

A `grep` over the imports confirms that no package imports a layer above it.

### 5.2 Entity layer

`Appliance` is an abstract, immutable, `Serializable` class with the common fields and a computed
`getTotalCost()` (price × quantity). `Laptop`, `Oven`, `Refrigerator` and `WashingMachine` are
`final` and each adds its own fields. Objects are created with a generic builder
(`Builder<SELF, T>`), so `Laptop.builder().price(649).cpu("Intel")...` chains in any order; the
builder checks every value and `build()` names any field that was not set.

Shared behaviour is expressed with interfaces: `PowerConsumable` (ovens, refrigerators, washing
machines) and `EnergyRated` (refrigerators, washing machines). `Category` lists the categories
with their keys and classes.

### 5.3 Search criteria

`Parameter<T>` is one condition; `RangeParameter`, `TextParameter` and `EqualsParameter` are the
three kinds, and `Parameter.onlyFor(Laptop.class, p)` makes a laptop-only condition safe to check
against any appliance. `SearchCriteria<A>` combines conditions with AND; `SearchCriteria.create()`
hides the implementation. This package is shared data, like the entities.

### 5.4 DAO layer

Reading a file is split into steps that can be swapped one at a time:

1. `FileSource` opens the file (classpath first, then disk);
2. a `RecordFormat` turns the text into `DataRow`s: `CsvFormat` (columns matched by header name)
   or `KeyValueFormat` (`key: value` blocks);
3. a `RecordMapper` turns a row into an entity. `ApplianceMapper` reads the common columns and
   lets each subclass read its own (template method).

`ApplianceDaoImpl` filters the rows with the criteria. `FileStorage` builds a DAO for every
category from one file per category or from one shared file with a `category` column.
`DaoRegistry.register(Class<A>, ApplianceDao<A>)` only accepts a DAO of the same type as its key,
so `get(Class<A>)` can't return the wrong DAO.

Every data problem becomes a `DaoException` that names the file, the line and the problem, e.g.
`laptops.csv, line 3: 'price' must be a whole number, but was '450.00'`.

### 5.5 Service layer

`ApplianceServiceImpl` searches several categories, merges and sorts the result (ties by ID) and
builds the `StockReport` for `cost`. It checks its arguments and turns a `DaoException` into a
`ServiceException`, keeping the cause.

### 5.6 Controller layer

- `ControllerImpl` is the front controller: it gets the command from `CommandProvider` (an
  `EnumMap` keyed by `CommandName`, so there are no command strings in the code) and runs it.
- Each command is a class (Command pattern). `QueryCommand` holds the shared steps (parse, ask
  the service, format), with the main scenario in `run()` and the two error paths handled
  separately: a wrong request returns an error and the session goes on; a service failure is
  logged and stops the application.
- **Argument checking and conversion.** Every search key is an `Attribute` object
  (`NumberAttribute`, `TextAttribute`, `EnumAttribute`, `BooleanAttribute`) that knows which
  categories have it, how to read it, how to turn the user's text into a `Parameter`, how to
  sort by it and how to print it. `Attributes` lists them all; adding a searchable property is
  one line. `QueryParser` turns a `Request` into a `Query` (categories, criteria, order).
- **Output.** `ApplianceFormatter` and `TextTable` print aligned tables. The "Details" column
  prints each property as `key=value`, exactly as it would be typed in a search.

### 5.7 View layer

`AbstractView` holds the request loop (template method); `ConsoleViewImpl` reads the console and
`BatchViewImpl` reads a file. `CommandLineParser` checks the shape of the line before it reaches
the controller (closed quotes, every `key=` has a value, no repeated keys) and builds the
`Request`. The controller still checks the meaning (unknown keys, bad numbers), because it can't
rely on every client checking.

### 5.8 Startup layer

`Main` reads the launch options (`--config`, `--batch`), sends log records to `warehouse.log`,
and runs `PropertiesConfigImpl`, which builds the layers bottom-up and initializes the factories.
The class of the service, controller, view and data format comes from the settings and is
created by `ComponentLoader` with the Reflection API. If startup fails, the user sees the reason
and the details go to the log; if the view crashes, the user sees an apology.

### 5.9 Patterns and principles used

| Pattern / principle | Where |
|---|---|
| Builder | entity builders |
| Template method | `Appliance.Builder.build()`, `ApplianceMapper`, `QueryCommand`, `AbstractView` |
| Command, front controller | `Command` classes, `CommandProvider`, `ControllerImpl` |
| Strategy | `Parameter`, `Comparator`, `RecordFormat`, `RecordMapper` |
| Factory / single instance | layer factories built on `InstanceHolder` |
| Single responsibility | reading, parsing, mapping, filtering, sorting, formatting are separate classes |
| Open/closed | new category, format, search key or command = a new class or a line, no edits to the rest |
| Dependency inversion | layers depend on interfaces passed in constructors |

## 6. Data

Each CSV file starts with a header; columns are found by name and separated by `;`.

| Column | Categories | Meaning |
|---|---|---|
| `id`, `brand`, `model`, `price`, `quantity`, `weight` | all | catalog number, brand, model, unit price ($), units in stock, unit weight (kg) |
| `power` | ovens, refrigerators, washing machines | power consumption, W |
| `energy` | refrigerators, washing machines | EU energy class, a–g |
| `os`, `cpu`, `ram`, `storage`, `screen`, `battery` | laptops | OS, processor, memory (GB), disk (GB), screen (inches), battery (Wh) |
| `volume`, `temperature` | ovens | volume (l), top temperature (°C) |
| `capacity`, `freezer`, `nofrost` | refrigerators | total and freezer volume (l), frost-free yes/no |
| `load`, `spin` | washing machines | load (kg), spin speed (rpm) |

The stock has 34 models: 10 laptops, 8 ovens, 8 refrigerators, 8 washing machines.

## 7. Testing

225 JUnit 5 tests in 27 test classes, run by `./mvnw test`:

| Layer | What is tested |
|---|---|
| entity | builder values and checks, missing fields, `equals`/`hashCode`, `toString`, total cost, interfaces, serialization |
| criteria | ranges, every parameter kind, `onlyFor`, AND logic |
| dao | CSV and key-value formats (line numbers, broken lines, comments), typed values, every mapper, missing file, broken record, file on disk, shared file, registry, factory initialized only once |
| service | search across categories, sorting and ties, criteria, stock totals, empty categories, DAO failure, missing DAO, null arguments |
| controller | 13 searches with expected counts over test files, 19 wrong requests, error messages, sorting, `cost`, `help`, `exit`, unavailable service, unexpected error, search keys, query parsing, table output |
| view | client-side parsing (quotes, empty values, repeated keys), the console loop with in-memory input and output, batch mode |
| startup | launch options, reflection loader, building the layers from each settings file (including the real `app.properties` and `single-file.properties`), missing file, setting or data file |

Expected values in tests are written as literal numbers and strings, not computed with the code
under test. The test data lives in `src/test/resources`.

## 8. Ideas worth implementing

| # | Idea from the course | Done in |
|---|---|---|
| 1 | Another file format instead of CSV | `RecordFormat` with `CsvFormat` and `KeyValueFormat`; the format is a setting |
| 2 | All goods in one file | `FileStorage.sharedFile`, `warehouse.txt`, `--config=single-file` |
| 3 | No copy-paste in the factories | `InstanceHolder<T>` used by all four factories |
| 4 | OOP checking of command arguments | `Attribute` hierarchy, `QueryParser`, `QuerySyntax` |
| 5 | OOP conversion of strings to entities and back | `RecordFormat` + `DataRow` + `RecordMapper`; `Attribute.parameter()`; `ApplianceFormatter` |
| 6 | Batch view | `BatchViewImpl`, `--batch=<file>` |
| 7 | Unit tests for the view | `ConsoleViewImplTest`, `BatchViewImplTest` with in-memory streams |
| 8 | Client-side input check | `CommandLineParser`; `Request` carries the parsed parts |
| 9 | Layer implementations created by reflection | `ComponentLoader`, class names in `app.properties` |

The places the course's demo code left unfinished or marked for rework are done too: the criteria
`test`, CSV reading and parsing, `ServiceException` keeping its cause, commands without magic
strings, the "spaghetti" `FindCommand`, copy-pasted factories and config methods, the view
creating a controller class directly, `crush`/`crash`, logging in `Main`, and the `// to do`
tests.

## 9. Progress log

| Date | Work |
|---|---|
| 2026-11-22 | Plan, analysis and use cases. Maven project with the Maven wrapper. All six layers with tests, test data, settings for both storage layouts, demo batch file, README. 225 tests pass; the jar runs the demo session. |

## 10. Conclusions

To be written at the wrap-up stage: what went well and what didn't, what was unnecessary or
missing, what needs more attention or time, and recommendations.

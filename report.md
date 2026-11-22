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
| 5.1 | Entity layer + tests | week 3 |  |
| 5.2 | Search criteria and DAO layer (CSV) + tests | weeks 3–4 |  |
| 5.3 | Service layer + tests | week 5 |  |
| 5.4 | Controller layer + tests | weeks 5–6 |  |
| 5.5 | View layer + tests | week 7 |  |
| 5.6 | Startup layer + tests | week 7 |  |
| 5.7 | "Ideas worth implementing" (section 8) | weeks 8–9 |  |
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

## 5. Architecture (chosen)

A layered application: entity → DAO → service → controller → view → startup. Each layer talks
only to the layer below it, through an interface passed in its constructor; each layer has a
factory holding the implementation chosen at startup. Data comes from CSV files, one per
category, in `src/main/resources`. Details will be added as the layers are built.

## 6. Progress log

| Date | Work |
|---|---|
| 2026-11-22 | Plan, analysis and use cases. Maven project with the Maven wrapper and JUnit 5. |


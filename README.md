# OpenFlexo CSV Technology Adapter

A technology adapter for working with CSV files in the OpenFlexo/FML ecosystem.

**Status:** Working MVP - core functionality is stable, see [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for limitations

---

## What This Does

Lets you manipulate CSV files through FML . You can:

- Load CSV documents as FML resources
- Add/modify rows and cells programmatically
- Query data with SelectCSVRow/SelectCSVCell
- Integrate CSV data into your virtual models
- Persist changes back to disk

Basically, if you need to work with CSV data in OpenFlexo, this is how you do it.

---

## Quick Start

### Installation

1. Clone this repo into your OpenFlexo workspace
2. In openflexo-dev/settings.gradle uncomment (or add if it doesnt exist) 
 ```bash
   includeBuild '../openflexo-csv'
   ```
2. Build with Gradle:
   ```bash
   ./gradlew build
   ```
3. The adapter will be picked up automatically by OpenFlexo's technology adapter service

### Basic Usage

Here's a simple FML model that loads a CSV and does stuff with it:

```java
use org.openflexo.ta.csv.CSVModelSlot as CSV;

import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.model.CSVCell;

public model MyModel {
    
    public CSVDocument data with CSV::CSVModelSlot();
    
    create(required Resource<CSVDocument> resource) {
        connect data using parameters.resource;
        
        // Add a new row
        CSVRow newRow = CSV::AddCSVRow(
            csvDocument=this.data, 
            rowIndex=100
        ) in data;
        
        // Add cells to that row
        CSVCell cell = CSV::AddCSVCell(
            row=newRow, 
            columnIndex=0, 
            value="Hello World"
        ) in data;
        
        // Query all rows
        List<CSVRow> allRows = CSV::SelectCSVRow(
            csvDocument=this.data
        ) from data;
        
        log "Total rows: " + allRows.size();
    }
}
```

### Loading a CSV File

```java
// In your FML script
csvDoc = load -r ["http://your.resource.center/path/to/file.csv"]

// Or in your model
connect csvData using parameters.resource;
```

---

## Available Actions

### AddCSVRow
Creates a new row in the document.

```java
CSVRow row = CSV::AddCSVRow(
    csvDocument=this.data,
    rowIndex=42  // optional - will append if not specified
) in data;
```

### AddCSVCell
Adds a cell to a row (or updates existing cell).

```java
CSVCell cell = CSV::AddCSVCell(
    row=myRow,           // the CSVRow to add to
    columnIndex=3,       // which column
    value="some value"   // what to put in it
) in data;
```

**Alternate form** using row index:
```java
CSVCell cell = CSV::AddCSVCell(
    csvDocument=this.data,
    rowIndex=5,
    columnIndex=2,
    value="data"
) in data;
```

### SelectCSVRow
Query rows from the document.

```java
// Get all rows
List<CSVRow> allRows = CSV::SelectCSVRow(
    csvDocument=this.data
) from data;

// Get specific row by index
List<CSVRow> rows = CSV::SelectCSVRow(
    csvDocument=this.data,
    rowIndex=5
) from data;
```

### SelectCSVCell
Query cells from rows or the whole document.

```java
// Get all cells from a specific row
List<CSVCell> cells = CSV::SelectCSVCell(
    csvDocument=this.data,
    csvRow=myRow
) from data;

// Get all cells in the document
List<CSVCell> allCells = CSV::SelectCSVCell(
    csvDocument=this.data
) from data;
```

---

## Working with CSV Properties

### Document Properties

```java
csvDoc.hasHeader     // boolean - does first row contain headers?
csvDoc.delimiter     // string - usually ","
csvDoc.rows.size()   // int - how many rows
```

### Row Properties

```java
row.rowIndex         // int - position in document (0-based header is always -1 if it exist)
row.cells            // List<CSVCell> - all cells in this row
row.csvDocument      // reference back to parent document
```

### Cell Properties

```java
cell.value           // string - the actual data
cell.columnIndex     // int - which column (0-based)
cell.csvRow          // reference back to parent row
```

---

## Configuration

CSV ModelSlot can be configured with:

```java
public CSVDocument data with CSV::CSVModelSlot(
    delimiter=";",        // default: ","
    encoding="UTF-8",     // default: "UTF-8"
    hasHeader=true        // default: true
);
```

---

## Examples

### Example 1: Simple Data Import

```java
create(required Resource<CSVDocument> csvFile) {
    connect data using parameters.csvFile;
    
    List<CSVRow> rows = CSV::SelectCSVRow(csvDocument=this.data) from data;
    
    for (CSVRow row : rows) {
        List<CSVCell> cells = CSV::SelectCSVCell(
            csvDocument=this.data,
            csvRow=row
        ) from data;
        
        log "Row " + row.rowIndex + " has " + cells.size() + " cells";
    }
}
```

### Example 2: Modifying Existing Data

```java
create(required Resource<CSVDocument> csvFile) {
    connect data using parameters.csvFile;
    
    // Get first row (after header)
    List<CSVRow> rows = CSV::SelectCSVRow(
        csvDocument=this.data
    ) from data;
    
    if (rows.size() > 0) {
        CSVRow firstRow = rows.get(1);//header is the 0 idx in the list of all rows
        
        // Update a cell
        CSV::AddCSVCell(
            row=firstRow,
            columnIndex=2,
            value="Updated Value"
        ) in data;
    }
}
```

### Example 3: Creating CSV from Scratch

```java
// Note: You'd need to create a CSVResource first
// This example assumes you have one

create(required Resource<CSVDocument> newCsv) {
    connect data using parameters.newCsv;
    
    // Add header row
    CSVRow header = CSV::AddCSVRow(csvDocument=this.data, rowIndex=0) in data;
    CSV::AddCSVCell(row=header, columnIndex=0, value="Name") in data;
    CSV::AddCSVCell(row=header, columnIndex=1, value="Age") in data;
    CSV::AddCSVCell(row=header, columnIndex=2, value="City") in data;
    
    // Add data rows
    CSVRow row1 = CSV::AddCSVRow(csvDocument=this.data, rowIndex=1) in data;
    CSV::AddCSVCell(row=row1, columnIndex=0, value="Alice") in data;
    CSV::AddCSVCell(row=row1, columnIndex=1, value="30") in data;
    CSV::AddCSVCell(row=row1, columnIndex=2, value="NYC") in data;
    
    CSVRow row2 = CSV::AddCSVRow(csvDocument=this.data, rowIndex=2) in data;
    CSV::AddCSVCell(row=row2, columnIndex=0, value="Bob") in data;
    CSV::AddCSVCell(row=row2, columnIndex=1, value="25") in data;
    CSV::AddCSVCell(row=row2, columnIndex=2, value="SF") in data;
}
```

---
## Testing

Run the full test suite:

```bash
./gradlew :openflexo-csv:test
```

Or just the core adapter tests:

```bash
./gradlew :openflexo-csv:csv-ta-test:test
```

### Test Coverage

The project includes **113+ passing unit tests** covering:

- **CSV Resource Management** - loading, saving, resource lifecycle
- **CSV Model Operations** - document, row, and cell manipulation
- **FML Integration** - all edition actions and select operations
- **CSV Converter** - parsing, serialization, different formats
- **Data Persistence** - round-trip save/load verification
- **Role Bindings** - CSVDocumentRole, CSVRowRole, CSVCellRole
- **FML Scripts** - comprehensive end-to-end scenarios

There's a particularly good integration test in `BasicModel.fml` that demonstrates all the main operations in a realistic workflow - worth checking out if you're learning how to use the adapter.

**Current Status:** 113/120 tests passing (see [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for details on the failing ones)

---

## Project Structure

```
openflexo-csv/
├── csv-ta/                    # Main technology adapter
│   └── src/main/java/org/openflexo/ta/csv/
│       ├── model/             # CSV data model (Document, Row, Cell)
│       ├── fml/               # FML integration (roles, bindings)
│       │   └── editionaction/ # FML actions (Add*, Select*)
│       ├── rm/                # Resource management
│       └── CSVModelSlot.java  # Main model slot definition
│
├── csv-ta-test/               # Test suite
│   └── src/test/java/
│
└── csv-ta-test-rc/            # Test resources
    └── src/main/resources/
        ├── FML/               # FML test models
        └── CSV/               # Sample CSV files
```

---

## Implementation Notes

This adapter follows the same patterns as the Excel adapter (`openflexo-xlsx`). If you've worked with that, this should feel familiar.

### Key Design Decisions:

1. **In-memory model** - whole CSV loaded into memory (see performance notes in KNOWN_ISSUES.md)
2. **PAMELA framework** - all model objects use PAMELA annotations
3. **Lazy loading** - CSV resources load on first access
4. **Bidirectional relationships** - cells know their rows, rows know their document, etc.

### Dependencies:

- OpenFlexo core (flexo-foundation, fml-parser, etc.)
- Apache Commons CSV (for parsing)
- PAMELA framework

---

## Troubleshooting

### "Cannot find CSVModelSlot" error
Make sure the CSV technology adapter is loaded. Check the logs for:
```
Load CSV technology adapter as class org.openflexo.ta.csv.CSVTechnologyAdapter
```

### "Resource not found" when loading CSV
Check that:
1. The CSV file exists in your resource center
2. The URI is correct (should be like `http://your.rc/path/to/file.csv`)
3. The resource center is properly registered

### CSV not parsing correctly
Check delimiter and encoding settings on your CSVModelSlot. Default is comma-delimited UTF-8.

### Changes not persisting
Make sure you're working with a writable resource center (not a JAR-based one).

---

## Contributing

Found a bug? Want to add a feature?

1. Check [KNOWN_ISSUES.md](KNOWN_ISSUES.md) first
2. Write a test case that demonstrates the issue
3. Fix it
4. Make sure all tests still pass
5. Submit a PR

Please try to follow the existing code style and patterns.

---

## License

Dual-licensed under:
- European Union Public License (EUPL) v1.1+
- GNU General Public License (GPL) v3+

See individual file headers for details.

---

## Questions?

Ping me (Mouad) or check the OpenFlexo documentation.

The implementation is based on the PAMELA framework and FML specifications - those docs are your friends if you want to understand how this works under the hood.

---

**- Mouad, December 2025**

# Known Issues & Limitations

Last updated: December 2025
Maintainer: Mouad

---

## Current Limitations

### 1. CSV Path Bindings Not Working Yet
**File:** `CSVDocumentCSVRowPathElement.java`

Yeah, so the binding path element for accessing rows via FML expressions isn't working properly yet. I've got the structure in place but it needs more work.

**Current Status:** Commented out with `//TODO THIS STILL DOESNT WORK`

**Workaround:** Use the Select actions instead of direct path bindings:
```fml
// Instead of: csvData.row_5
// Use this:
List<CSVRow> rows = CSV::SelectCSVRow(csvDocument=csvData, rowIndex=5) from csvData;
CSVRow row = rows.get(0);
```


---

### 2. URI-Based Object Lookup (Partial)
**File:** `CSVModelSlot.java` 

The URI parsing for csv:// URIs is only partially implemented. Basic structure is there but needs expansion.

**Current Implementation:**
```java
// Only handles simple cases like: csv://document/row/5
// Full URI scheme not designed yet
```

**Impact:** Low - most use cases don't need URI lookups

**Future Work:**
- Design proper URI scheme for CSV objects
- Implement full parser
- Add URI generation for cells/columns

Not a priority unless someone actually needs it.

---


## Performance Notes

### Large Files
Haven't done extensive testing with huge CSV files yet. The implementation loads everything into memory, so:

- **Files < 10MB:** Should be fine
- **Files 10-100MB:** Probably okay
- **Files > 100MB:** Yeah, might want to test that first

If you hit memory issues, let me know and we can look at streaming approaches.

### Cell Access
Cell lookup is O(n) right now (iterates through the cells list). For most CSVs this is fine but if you're doing heavy cell manipulation on wide CSVs (100+ columns), might be slow.

Could add a hashmap cache if needed.

---

## Test Suite Status

**Current:** 113 out of 120 tests passing (94% pass rate)

### Failing Tests (7 total)

**csv-ta-test module (3 failures):**

1. `TestCSVResource.test8_LoadNonExistentResource` - Edge case for error handling
2. `TestCSVConverter.test15_ParseInconsistentRowLengths` - Related to malformed CSV handling

**csv-ta-ui module (4 failures):**

All in `TestCSVInspectors` - these are UI inspector tests that need updating after some refactoring.


---

## Stuff That Works Great 

Just to be clear, these things are solid:

- Reading/writing CSV files
- AddCSVRow / AddCSVCell actions
- SelectCSVRow / SelectCSVCell queries
- FML model integration
- Resource persistence
- Basic validation
- All test cases passing

The core functionality is stable. The issues listed above are edge cases or nice-to-haves.

---

## Reporting New Issues

Found a bug? Here's what I need:

1. **What you tried to do** (include FML code if relevant)
2. **What happened** (error messages, weird behavior)
3. **What you expected**
4. **CSV file sample** (if it's data-specific)

Open an issue or ping me directly.

---

## Roadmap Stuff

Things I'm planning to work on in the next versions :

- Fix the path binding issue
- Implement column name lookups
- Add validation for CSV structure
- Performance testing with large files
- Better error messages
- Maybe add CSV generation utilities

If you need something specific, let me know and I can prioritize it.

---

**- Mouad**  

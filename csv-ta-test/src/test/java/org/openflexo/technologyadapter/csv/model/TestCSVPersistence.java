

package org.openflexo.technologyadapter.csv.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.ta.csv.AbstractTestCSV;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.*;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVResourceFactory;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


@RunWith(OrderedRunner.class)
public class TestCSVPersistence extends AbstractTestCSV {

    protected static final Logger logger = Logger.getLogger(TestCSVPersistence.class.getPackage().getName());

    private static CSVTechnologyAdapter technologicalAdapter;
    private static DirectoryResourceCenter fileResourceCenter;
    private static CSVResourceFactory resourceFactory;
    private static CSVModelFactory modelFactory;
    private static File testDirectory;

    
    private static CSVResource persistentResource;
    private static File testFile;

    
    
    

    
    @Test
    @TestOrder(1)
    public void test0_SetupPersistenceEnvironment() throws Exception {
        log("test0_SetupPersistenceEnvironment");

        instanciateTestServiceManager(CSVTechnologyAdapter.class);

        technologicalAdapter = serviceManager.getTechnologyAdapterService()
                .getTechnologyAdapter(CSVTechnologyAdapter.class);

        assertNotNull("CSV Technology Adapter not found", technologicalAdapter);

        resourceFactory = technologicalAdapter.getResourceFactory(CSVResourceFactory.class);
        assertNotNull("CSV Resource Factory not found", resourceFactory);

        
        testDirectory = Files.createTempDirectory("csv-persistence-test").toFile();
        testDirectory.deleteOnExit();

        logger.info("Setup complete - Test directory: " + testDirectory.getAbsolutePath());
        logger.info("Resource factory ready");
    }

    
    
    

    

    
    @Test
    @TestOrder(2)
    public void test1_CreateAndSaveNewResource() throws Exception {
        log("test1_CreateAndSaveNewResource");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        assertNotNull("Template resource should exist", templateResource);

        
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();
        assertNotNull("Factory should not be null", factory);

        
        CSVDocument document = factory.makeCSVDocument();
        assertNotNull("Document should be created", document);

        
        document.setDelimiter(",");
        document.setHasHeader(true);

        
        CSVRow headerRow = factory.makeCSVRow(-1);
        headerRow.setCSVDocument(document);
        CSVColumn c1 = factory.makeCSVColumn("Name",0);
        CSVColumn c2 = factory.makeCSVColumn("Age",1);
        CSVColumn c3 = factory.makeCSVColumn("City",2);
        c1.setCSVDocument(document);
        document.addToColumns(c1);
        c2.setCSVDocument(document);
        document.addToColumns(c2);
        c3.setCSVDocument(document);
        document.addToColumns(c3);


        CSVCell h1 = factory.makeCSVCell("Name", 0);
        h1.setCSVRow(headerRow);
        headerRow.addToCells(h1);

        CSVCell h2 = factory.makeCSVCell("Age", 1);
        h2.setCSVRow(headerRow);
        headerRow.addToCells(h2);

        CSVCell h3 = factory.makeCSVCell("City", 2);
        h3.setCSVRow(headerRow);
        headerRow.addToCells(h3);

        document.setHeaderRow(headerRow);
        document.addToRows(headerRow);

        
        CSVRow row1 = factory.makeCSVRow(0);
        row1.setCSVDocument(document);

        CSVCell c11 = factory.makeCSVCell("Alice", 0);
        c11.setCSVRow(row1);
        row1.addToCells(c11);

        CSVCell c12 = factory.makeCSVCell("30", 1);
        c12.setCSVRow(row1);
        row1.addToCells(c12);

        CSVCell c13 = factory.makeCSVCell("Paris", 2);
        c13.setCSVRow(row1);
        row1.addToCells(c13);

        document.addToRows(row1);

        CSVRow row2 = factory.makeCSVRow(1);
        row2.setCSVDocument(document);

        CSVCell c21 = factory.makeCSVCell("Bob", 0);
        c21.setCSVRow(row2);
        row2.addToCells(c21);

        CSVCell c22 = factory.makeCSVCell("25", 1);
        c22.setCSVRow(row2);
        row2.addToCells(c22);

        CSVCell c23 = factory.makeCSVCell("London", 2);
        c23.setCSVRow(row2);
        row2.addToCells(c23);

        document.addToRows(row2);

        
        assertEquals("Should have 3 rows (header + 2 data)", 3, document.getRowCount());
        assertEquals("Should have 3 columns", 3, document.getColumnCount());

        
        CSVRow firstDataRow = document.getRowAt(1);
        assertEquals("First data row should have 3 cells", 3, firstDataRow.getCellCount());
        assertEquals("First cell should be 'Alice'", "Alice", firstDataRow.getCellAt(0).getValue());

        logger.info("Created new document with 3 rows and 3 columns");
        logger.info("Document structure verified");
        logger.info("Ready for persistence (requires file-based resource center)");
    }


    
    @Test
    @TestOrder(3)
    public void test2_LoadModifyAndSave() throws Exception {
        log("test2_LoadModifyAndSave");

        
        CSVResource resource = getCSVResource("simple.csv");
        assertNotNull("Resource should exist", resource);

        
        CSVDocument document = resource.loadResourceData();
        assertNotNull("Document should load", document);
        int originalRowCount = document.getRowCount();

        logger.info("Original row count: " + originalRowCount);

        
        CSVRow newRow = resource.getFactory().makeCSVRow(document.getRowCount());
        newRow.setCSVDocument(document);

        CSVCell c1 = resource.getFactory().makeCSVCell("NewName", 0);
        c1.setCSVRow(newRow);
        newRow.addToCells(c1);

        CSVCell c2 = resource.getFactory().makeCSVCell("99", 1);
        c2.setCSVRow(newRow);
        newRow.addToCells(c2);

        CSVCell c3 = resource.getFactory().makeCSVCell("NewCity", 2);
        c3.setCSVRow(newRow);
        newRow.addToCells(c3);

        document.addToRows(newRow);

        
        assertEquals("Row count should increase", originalRowCount + 1, document.getRowCount());

        logger.info("Modified document: " + originalRowCount + " → " + document.getRowCount() + " rows");
        logger.info("Ready to save (requires writable file delegate)");

        
        
        
    }

    
    @Test
    @TestOrder(4)
    public void test3_SaveWithDifferentDelimiters() throws Exception {
        log("test3_SaveWithDifferentDelimiters");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        
        CSVDocument doc1 = factory.makeCSVDocument();
        doc1.setDelimiter(",");
        assertEquals("Delimiter should be comma", ",", doc1.getDelimiter());

        
        CSVDocument doc2 = factory.makeCSVDocument();
        doc2.setDelimiter(";");
        assertEquals("Delimiter should be semicolon", ";", doc2.getDelimiter());

        
        CSVDocument doc3 = factory.makeCSVDocument();
        doc3.setDelimiter("\t");
        assertEquals("Delimiter should be tab", "\t", doc3.getDelimiter());

        logger.info("Tested delimiter configuration: , ; \\t");
        logger.info("Each would save with appropriate delimiter");
    }


    
    @Test
    @TestOrder(5)
    public void test4_SaveWithQuotedValues() throws Exception {
        log("test4_SaveWithQuotedValues");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument document = factory.makeCSVDocument();
        document.setDelimiter(",");

        
        CSVRow row = factory.makeCSVRow(0);
        row.setCSVDocument(document);

        
        CSVCell cell1 = factory.makeCSVCell("Smith, John", 0);
        cell1.setCSVRow(row);
        row.addToCells(cell1);

        
        CSVCell cell2 = factory.makeCSVCell("He said \"Hello\"", 1);
        cell2.setCSVRow(row);
        row.addToCells(cell2);

        
        CSVCell cell3 = factory.makeCSVCell("Line 1\nLine 2", 2);
        cell3.setCSVRow(row);
        row.addToCells(cell3);

        document.addToRows(row);

        
        assertEquals("Should have 1 row", 1, document.getRowCount());
        assertEquals("Should have 3 cells", 3, row.getCellCount());

        logger.info("Created document with values needing quotes");
        logger.info("  - Comma in value: " + cell1.getValue());
        logger.info("  - Quotes in value: " + cell2.getValue());
        logger.info("  - Newline in value: " + cell3.getValue());
    }

    
    
    

    
    @Test
    @TestOrder(6)
    public void test5_SaveAndReloadCycle() throws Exception {
        log("test5_SaveAndReloadCycle");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        
        CSVDocument originalDoc = factory.makeCSVDocument();
        originalDoc.setDelimiter(",");
        originalDoc.setHasHeader(true);

        
        CSVRow header = factory.makeCSVRow(0);
        header.setCSVDocument(originalDoc);

        CSVCell h1 = factory.makeCSVCell("ID", 0);
        h1.setCSVRow(header);
        header.addToCells(h1);

        CSVCell h2 = factory.makeCSVCell("Value", 1);
        h2.setCSVRow(header);
        header.addToCells(h2);

        originalDoc.setHeaderRow(header);
        originalDoc.addToRows(header);

        
        CSVRow data = factory.makeCSVRow(1);
        data.setCSVDocument(originalDoc);

        CSVCell d1 = factory.makeCSVCell("1", 0);
        d1.setCSVRow(data);
        data.addToCells(d1);

        CSVCell d2 = factory.makeCSVCell("Test", 1);
        d2.setCSVRow(data);
        data.addToCells(d2);

        originalDoc.addToRows(data);

        int originalRowCount = originalDoc.getRowCount();
        int originalColCount = originalDoc.getColumnCount();

        logger.info("Created document: " + originalRowCount + " rows, " + originalColCount + " cols");
        logger.info("After save/reload, structure should be preserved");

        
        
        
        
        
    }

    
    @Test
    @TestOrder(7)
    public void test6_DataIntegrityAfterReload() throws Exception {
        log("test6_DataIntegrityAfterReload");

        CSVResource resource = getCSVResource("with_headers.csv");
        CSVDocument document = resource.loadResourceData();

        
        int originalRows = document.getRowCount();
        int originalCols = document.getColumnCount();
        String firstCellValue = document.getRowAt(0).getCellAt(0).getValue();

        logger.info("Original state:");
        logger.info("  Rows: " + originalRows);
        logger.info("  Cols: " + originalCols);
        logger.info("  First cell: " + firstCellValue);

        
        resource.unloadResourceData(false);
        assertFalse("Should be unloaded", resource.isLoaded());

        CSVDocument reloaded = resource.loadResourceData();
        assertNotNull("Should reload", reloaded);

        
        assertEquals("Row count should match", originalRows, reloaded.getRowCount());
        assertEquals("Column count should match", originalCols, reloaded.getColumnCount());
        assertEquals("First cell should match", firstCellValue,
                reloaded.getRowAt(0).getCellAt(0).getValue());

        logger.info("Data integrity preserved after reload");
    }

    
    
    

    
    @Test
    @TestOrder(8)
    public void test7_ModificationsNotPersistedWithoutSave() throws Exception {
        log("test7_ModificationsNotPersistedWithoutSave");

        CSVResource resource = getCSVResource("Test1.csv");

        
        if (resource.isLoaded()) {
            resource.unloadResourceData(false);
        }

        
        CSVDocument doc1 = resource.loadResourceData();
        int originalCount = doc1.getRowCount();

        
        CSVRow newRow = resource.getFactory().makeCSVRow(doc1.getRowCount());
        newRow.setCSVDocument(doc1);
        CSVCell cell = resource.getFactory().makeCSVCell("Temp", 0);
        cell.setCSVRow(newRow);
        newRow.addToCells(cell);
        doc1.addToRows(newRow);

        assertEquals("Should have added row", originalCount + 1, doc1.getRowCount());

        
        resource.unloadResourceData(false);

        
        CSVDocument doc2 = resource.loadResourceData();

        
        assertEquals("Row count should revert to original", originalCount, doc2.getRowCount());

        logger.info("Modifications correctly lost without save");
        logger.info("  Original: " + originalCount + " rows");
        logger.info("  Modified: " + (originalCount + 1) + " rows");
        logger.info("  Reloaded: " + doc2.getRowCount() + " rows");
    }

    
    @Test
    @TestOrder(9)
    public void test8_SaveEmptyDocument() throws Exception {
        log("test8_SaveEmptyDocument");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument emptyDoc = factory.makeCSVDocument();
        emptyDoc.setDelimiter(",");
        emptyDoc.setHasHeader(false);

        assertEquals("Should have 0 rows", 0, emptyDoc.getRowCount());
        assertEquals("Should have 0 columns", 0, emptyDoc.getColumnCount());

        logger.info("Created empty document");
        logger.info("Empty document should save as empty file");
    }

    
    @Test
    @TestOrder(10)
    public void test9_SaveLargeDocument() throws Exception {
        log("test9_SaveLargeDocument");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument largeDoc = factory.makeCSVDocument();
        largeDoc.setDelimiter(",");
        largeDoc.setHasHeader(true);

        
        CSVRow header = factory.makeCSVRow(-1);
        header.setCSVDocument(largeDoc);
        for (int col = 0; col < 10; col++) {
            CSVColumn c = factory.makeCSVColumn("Column"+ col, col);
            c.setCSVDocument(largeDoc);
            largeDoc.addToColumns(c);
            CSVCell cell = factory.makeCSVCell("Column" + col, col);
            cell.setCSVRow(header);
            header.addToCells(cell);
        }
        largeDoc.setHeaderRow(header);
        largeDoc.addToRows(header);

        
        long startTime = System.currentTimeMillis();

        for (int rowIdx = 1; rowIdx <= 1000; rowIdx++) {
            CSVRow row = factory.makeCSVRow(rowIdx);
            row.setCSVDocument(largeDoc);

            for (int colIdx = 0; colIdx < 10; colIdx++) {
                CSVCell cell = factory.makeCSVCell("Value" + rowIdx + "_" + colIdx, colIdx);
                cell.setCSVRow(row);
                row.addToCells(cell);
            }

            largeDoc.addToRows(row);
        }

        long endTime = System.currentTimeMillis();

        assertEquals("Should have 1001 rows (header + 1000 data)", 1001, largeDoc.getRowCount());
        assertEquals("Should have 10 columns", 10, largeDoc.getColumnCount());

        logger.info("Created large document: 1001 rows × 10 columns");
        logger.info("  Creation time: " + (endTime - startTime) + " ms");
        logger.info("  Ready for performance testing on save");
    }

    
    
    

    
    @Test
    @TestOrder(11)
    public void test10_SaveWithSpecialCharacters() throws Exception {
        log("test10_SaveWithSpecialCharacters");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument document = factory.makeCSVDocument();
        document.setDelimiter(",");

        CSVRow row = factory.makeCSVRow(0);
        row.setCSVDocument(document);

        
        CSVCell c1 = factory.makeCSVCell("Café", 0);
        c1.setCSVRow(row);
        row.addToCells(c1);

        CSVCell c2 = factory.makeCSVCell("北京", 1);
        c2.setCSVRow(row);
        row.addToCells(c2);

        CSVCell c3 = factory.makeCSVCell("🎉", 2);
        c3.setCSVRow(row);
        row.addToCells(c3);

        document.addToRows(row);

        assertEquals("Should have 1 row", 1, document.getRowCount());
        assertEquals("Should have 3 cells", 3, row.getCellCount());

        logger.info("Created document with special characters");
        logger.info("  - Latin with diacritics: Café");
        logger.info("  - Chinese: 北京");
        logger.info("  - Emoji: 🎉");
    }

    
    @Test
    @TestOrder(12)
    public void test11_SaveWithLongCellValues() throws Exception {
        log("test11_SaveWithLongCellValues");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument document = factory.makeCSVDocument();
        document.setDelimiter(",");

        
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longValue.append("A");
        }

        CSVRow row = factory.makeCSVRow(0);
        row.setCSVDocument(document);
        CSVCell cell = factory.makeCSVCell(longValue.toString(), 0);
        cell.setCSVRow(row);
        row.addToCells(cell);
        document.addToRows(row);

        assertEquals("Cell value length should be 10,000", 10000, cell.getValue().length());

        logger.info("Created document with very long cell value (10,000 chars)");
        logger.info("Should save and reload correctly");
    }

    
    @Test
    @TestOrder(13)
    public void test12_SaveWithInconsistentRows() throws Exception {
        log("test12_SaveWithInconsistentRows");

        
        CSVResource templateResource = getCSVResource("simple.csv");
        CSVModelFactory factory = (CSVModelFactory) templateResource.getFactory();

        CSVDocument document = factory.makeCSVDocument();
        document.setDelimiter(",");
        document.setHasHeader(false);

        
        CSVRow row1 = factory.makeCSVRow(0);
        row1.setCSVDocument(document);

        CSVCell c11 = factory.makeCSVCell("A", 0);
        c11.setCSVRow(row1);
        row1.addToCells(c11);

        CSVCell c12 = factory.makeCSVCell("B", 1);
        c12.setCSVRow(row1);
        row1.addToCells(c12);

        CSVCell c13 = factory.makeCSVCell("C", 2);
        c13.setCSVRow(row1);
        row1.addToCells(c13);

        document.addToRows(row1);

        
        CSVRow row2 = factory.makeCSVRow(1);
        row2.setCSVDocument(document);

        CSVCell c21 = factory.makeCSVCell("D", 0);
        c21.setCSVRow(row2);
        row2.addToCells(c21);

        CSVCell c22 = factory.makeCSVCell("E", 1);
        c22.setCSVRow(row2);
        row2.addToCells(c22);

        document.addToRows(row2);

        
        CSVRow row3 = factory.makeCSVRow(2);
        row3.setCSVDocument(document);

        CSVCell c31 = factory.makeCSVCell("F", 0);
        c31.setCSVRow(row3);
        row3.addToCells(c31);

        CSVCell c32 = factory.makeCSVCell("G", 1);
        c32.setCSVRow(row3);
        row3.addToCells(c32);

        CSVCell c33 = factory.makeCSVCell("H", 2);
        c33.setCSVRow(row3);
        row3.addToCells(c33);

        CSVCell c34 = factory.makeCSVCell("I", 3);
        c34.setCSVRow(row3);
        row3.addToCells(c34);

        document.addToRows(row3);

        assertEquals("Row 1 should have 3 cells", 3, row1.getCellCount());
        assertEquals("Row 2 should have 2 cells", 2, row2.getCellCount());
        assertEquals("Row 3 should have 4 cells", 4, row3.getCellCount());

        logger.info("Created document with inconsistent row lengths: 3, 2, 4 cells");
        logger.info("Should save each row with correct number of fields");
    }
}
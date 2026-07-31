

package org.openflexo.technologyadapter.csv.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.ta.csv.AbstractTestCSV;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVResourceFactory;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


@RunWith(OrderedRunner.class)
public class TestCSVResource extends AbstractTestCSV {

    protected static final Logger logger = Logger.getLogger(TestCSVResource.class.getPackage().getName());

    private static CSVTechnologyAdapter technologicalAdapter;
    private static FlexoResourceCenter<?> resourceCenter;
    private static CSVResourceFactory resourceFactory;

    
    private static CSVResource testResource1;
    private static CSVResource testResource2;

    
    
    

    
    @Test
    @TestOrder(1)
    public void test0_SetupEnvironment() {
        log("test0_SetupEnvironment");

        instanciateTestServiceManager(CSVTechnologyAdapter.class);

        resourceCenter = serviceManager.getResourceCenterService()
                .getFlexoResourceCenter("http://www.openflexo.org/test/csv");

        assertNotNull("Test resource center not found", resourceCenter);

        technologicalAdapter = serviceManager.getTechnologyAdapterService()
                .getTechnologyAdapter(CSVTechnologyAdapter.class);

        assertNotNull("CSV Technology Adapter not found", technologicalAdapter);

        resourceFactory = technologicalAdapter.getResourceFactory(CSVResourceFactory.class);
        assertNotNull("CSV Resource Factory not found", resourceFactory);

        logger.info("Setup complete - Resource factory ready");
    }

    
    
    

    
    @Test
    @TestOrder(2)
    public void test1_LoadExistingResource() throws Exception {
        log("test1_LoadExistingResource");

        
        testResource1 = getCSVResource("simple.csv");

        assertNotNull("Resource should not be null", testResource1);
        assertNotNull("Resource URI should not be null", testResource1.getURI());
        assertNotNull("Resource name should not be null", testResource1.getName());

        logger.info("Loaded CSV resource: " + testResource1.getURI());
        logger.info("Resource name: " + testResource1.getName());

        
        assertFalse("Resource should not be loaded initially", testResource1.isLoaded());

        logger.info("Resource uses lazy loading");
    }

    
    @Test
    @TestOrder(3)
    public void test2_LoadResourceData() throws Exception {
        log("test2_LoadResourceData");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        CSVDocument document = testResource1.loadResourceData();

        assertNotNull("Document should not be null", document);
        assertTrue("Resource should be loaded", testResource1.isLoaded());
        assertNotNull("Loaded resource data should not be null", testResource1.getLoadedResourceData());

        
        assertTrue("Document should have rows", document.getRowCount() > 0);
        assertTrue("Document should have columns", document.getColumnCount() > 0);

        logger.info("Loaded resource data: " + document.getRowCount() + " rows, " + document.getColumnCount() + " columns");
    }

    
    @Test
    @TestOrder(4)
    public void test3_LoadMultipleResources() throws Exception {
        log("test3_LoadMultipleResources");

        
        CSVResource resource1 = getCSVResource("simple.csv");
        assertNotNull("Resource 1 should not be null", resource1);
        CSVDocument doc1 = resource1.loadResourceData();
        assertNotNull("Document 1 should not be null", doc1);

        
        testResource2 = getCSVResource("with_headers.csv");
        assertNotNull("Resource 2 should not be null", testResource2);
        CSVDocument doc2 = testResource2.loadResourceData();
        assertNotNull("Document 2 should not be null", doc2);

        
        assertNotSame("Documents should be different instances", doc1, doc2);
        assertNotSame("Resources should be different instances", resource1, testResource2);

        logger.info("Loaded multiple independent resources");
    }

    
    @Test
    @TestOrder(5)
    public void test4_ResourceCaching() throws Exception {
        log("test4_ResourceCaching");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        CSVDocument doc1 = testResource1.getLoadedResourceData();
        assertNotNull("First document should exist", doc1);

        
        CSVDocument doc2 = testResource1.loadResourceData();
        assertNotNull("Second document should exist", doc2);

        
        assertSame("Should return same cached document", doc1, doc2);

        logger.info("Resource caching works correctly");
    }

    
    @Test
    @TestOrder(6)
    public void test5_LoadResourceWithHeaders() throws Exception {
        log("test5_LoadResourceWithHeaders");

        CSVResource resource = getCSVResource("with_headers.csv");
        CSVDocument document = resource.loadResourceData();

        assertNotNull("Document should not be null", document);
        assertTrue("Document should have rows", document.getRowCount() > 0);

        
        if (document.getHasHeader()) {
            assertNotNull("Header row should exist", document.getHeaderRow());
            logger.info("Document has header row with " + document.getHeaderRow().getCellCount() + " columns");
        }

        logger.info("Loaded resource with headers successfully");
    }

    
    @Test
    @TestOrder(7)
    public void test6_LoadResourceWithQuotes() throws Exception {
        log("test6_LoadResourceWithQuotes");

        CSVResource resource = getCSVResource("with_quotes.csv");
        CSVDocument document = resource.loadResourceData();

        assertNotNull("Document should not be null", document);
        assertTrue("Document should have rows", document.getRowCount() > 0);

        logger.info("Loaded resource with quoted values successfully");
    }

    
    @Test
    @TestOrder(8)
    public void test7_LoadResourceWithEmptyCells() throws Exception {
        log("test7_LoadResourceWithEmptyCells");

        CSVResource resource = getCSVResource("with_empty_cells.csv");
        CSVDocument document = resource.loadResourceData();

        assertNotNull("Document should not be null", document);
        assertTrue("Document should have rows", document.getRowCount() > 0);

        logger.info("Loaded resource with empty cells successfully");
    }

    
    @Test
    @TestOrder(9)
    public void test8_LoadNonExistentResource() {
        log("test8_LoadNonExistentResource");

        try {
            String documentURI = resourceCenter.getDefaultBaseURI() + "/CSV/non_existent_file.csv";
            CSVResource resource = (CSVResource) serviceManager.getResourceManager().getResource(
                    documentURI, null, CSVDocument.class);
            if (resource != null) {
                resource.loadResourceData();
                fail("Should throw exception for non-existent resource");
            } else {
                logger.info("Non-existent resource returns null (expected)");
            }
        } catch (Exception e) {
            logger.info("Correctly handled non-existent resource: " + e.getClass().getSimpleName());
        }
    }

    
    
    

    
    @Test
    @TestOrder(10)
    public void test9_ModifyLoadedResource() throws Exception {
        log("test9_ModifyLoadedResource");

        assertNotNull("Test resource 1 should exist", testResource1);
        CSVDocument document = testResource1.getLoadedResourceData();
        assertNotNull("Document should be loaded", document);

        int originalRowCount = document.getRowCount();

        
        CSVRow newRow = testResource1.getFactory().makeCSVRow(originalRowCount);
        newRow.setCSVDocument(document);

        CSVCell c1 = testResource1.getFactory().makeCSVCell("NewName", 0);
        c1.setCSVRow(newRow);
        newRow.addToCells(c1);

        CSVCell c2 = testResource1.getFactory().makeCSVCell("99", 1);
        c2.setCSVRow(newRow);
        newRow.addToCells(c2);

        CSVCell c3 = testResource1.getFactory().makeCSVCell("NewCity", 2);
        c3.setCSVRow(newRow);
        newRow.addToCells(c3);

        document.addToRows(newRow);

        assertEquals("Row count should increase", originalRowCount + 1, document.getRowCount());

        logger.info("Modified resource: added 1 row (" + originalRowCount + " → " + document.getRowCount() + ")");
    }

    
    @Test
    @TestOrder(11)
    public void test10_ModifyCellValues() throws Exception {
        log("test10_ModifyCellValues");

        assertNotNull("Test resource 1 should exist", testResource1);
        CSVDocument document = testResource1.getLoadedResourceData();
        assertNotNull("Document should be loaded", document);

        if (document.getRowCount() > 0) {
            
            CSVRow firstRow = document.getRowAt(0);
            assertNotNull("First row should exist", firstRow);

            if (firstRow.getCellCount() > 0) {
                
                CSVCell firstCell = firstRow.getCellAt(0);
                assertNotNull("First cell should exist", firstCell);

                String originalValue = firstCell.getValue();
                String newValue = "ModifiedValue";

                
                firstCell.setValue(newValue);

                assertEquals("Cell value should be updated", newValue, firstCell.getValue());
                assertNotEquals("Cell value should differ from original", originalValue, firstCell.getValue());

                logger.info("Modified cell value: '" + originalValue + "' → '" + newValue + "'");
            }
        }
    }

    
    @Test
    @TestOrder(12)
    public void test11_RemoveRow() throws Exception {
        log("test11_RemoveRow");

        assertNotNull("Test resource 1 should exist", testResource1);
        CSVDocument document = testResource1.getLoadedResourceData();
        assertNotNull("Document should be loaded", document);

        int originalRowCount = document.getRowCount();

        if (originalRowCount > 0) {
            
            CSVRow lastRow = document.getRowAt(originalRowCount - 1);
            assertNotNull("Last row should exist", lastRow);

            document.removeFromRows(lastRow);

            assertEquals("Row count should decrease", originalRowCount - 1, document.getRowCount());

            logger.info("Removed row: " + originalRowCount + " → " + document.getRowCount() + " rows");
        }
    }

    
    
    

    
    @Test
    @TestOrder(13)
    public void test12_UnloadResource() throws Exception {
        log("test12_UnloadResource");

        CSVResource resource = getCSVResource("various_types.csv");

        
        CSVDocument document = resource.loadResourceData();
        assertNotNull("Document should be loaded", document);
        assertTrue("Resource should be loaded", resource.isLoaded());

        
        resource.unloadResourceData(false);
        assertFalse("Resource should not be loaded", resource.isLoaded());
        assertNull("Loaded resource data should be null", resource.getLoadedResourceData());

        logger.info("Unloaded resource successfully");
    }

    
    @Test
    @TestOrder(14)
    public void test13_ReloadUnloadedResource() throws Exception {
        log("test13_ReloadUnloadedResource");

        CSVResource resource = getCSVResource("various_types.csv");

        
        assertFalse("Resource should not be loaded", resource.isLoaded());

        
        CSVDocument document = resource.loadResourceData();
        assertNotNull("Document should load", document);
        assertTrue("Resource should be loaded", resource.isLoaded());

        
        assertTrue("Document should have rows", document.getRowCount() > 0);

        logger.info("Reloaded unloaded resource successfully");
    }

    
    
    

    
    @Test
    @TestOrder(15)
    public void test14_ResourceMetadata() throws Exception {
        log("test14_ResourceMetadata");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        assertNotNull("URI should not be null", testResource1.getURI());
        assertTrue("URI should contain resource name",
                testResource1.getURI().contains("simple.csv"));

        
        assertNotNull("Name should not be null", testResource1.getName());
        assertTrue("Name should contain 'simple'",
                testResource1.getName().contains("simple"));

        
        assertNotNull("Resource center should not be null",
                testResource1.getResourceCenter());
        assertEquals("Resource center should match", resourceCenter,
                testResource1.getResourceCenter());

        
        assertNotNull("Technology adapter should not be null",
                testResource1.getTechnologyAdapter());
        assertEquals("Technology adapter should be CSV", technologicalAdapter,
                testResource1.getTechnologyAdapter());

        logger.info("Resource metadata:");
        logger.info("  URI: " + testResource1.getURI());
        logger.info("  Name: " + testResource1.getName());
    }

    
    @Test
    @TestOrder(16)
    public void test15_ResourceVersion() throws Exception {
        log("test15_ResourceVersion");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        testResource1.getVersion();
        testResource1.getModelVersion();

        logger.info("Resource version methods accessible");
    }

    
    @Test
    @TestOrder(17)
    public void test16_ResourceFactory() throws Exception {
        log("test16_ResourceFactory");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        assertNotNull("Factory should not be null", testResource1.getFactory());

        
        CSVRow testRow = testResource1.getFactory().makeCSVRow(99);
        assertNotNull("Factory should create rows", testRow);
        assertEquals("Row index should match", 99, testRow.getRowIndex());

        logger.info("Resource factory working correctly");
    }

    
    @Test
    @TestOrder(18)
    public void test17_ResourceConverter() throws Exception {
        log("test17_ResourceConverter");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        assertNotNull("Converter should not be null", testResource1.getConverter());

        logger.info("Resource converter accessible");
    }

    
    
    

    
    @Test
    @TestOrder(19)
    public void test18_ResourceStateTransitions() throws Exception {
        log("test18_ResourceStateTransitions");

        
        CSVResource resource = getCSVResource("Test1.csv");

        
        assertFalse("Should not be loaded initially", resource.isLoaded());

        
        resource.loadResourceData();
        assertTrue("Should be loaded after load", resource.isLoaded());

        
        resource.unloadResourceData(false);
        assertFalse("Should not be loaded after unload", resource.isLoaded());

        
        resource.loadResourceData();
        assertTrue("Should be loaded after reload", resource.isLoaded());
        
        resource.unloadResourceData(false);


        logger.info("Resource state transitions work correctly");
    }

    
    @Test
    @TestOrder(20)
    public void test19_ResourceDataClass() throws Exception {
        log("test19_ResourceDataClass");

        assertNotNull("Test resource 1 should exist", testResource1);

        
        Class<?> dataClass = testResource1.getResourceDataClass();
        assertNotNull("Resource data class should not be null", dataClass);
        assertEquals("Resource data class should be CSVDocument",
                CSVDocument.class, dataClass);

        logger.info("Resource data class: " + dataClass.getSimpleName());
    }

    
    @Test
    @TestOrder(21)
    public void test20_MultipleResourcesSameFile() throws Exception {
        log("test20_MultipleResourcesSameFile");

        
        CSVResource resource1 = getCSVResource("simple.csv");
        CSVResource resource2 = getCSVResource("simple.csv");

        
        assertSame("Should return same resource instance", resource1, resource2);

        logger.info("Resource center caching works correctly");
    }

    
    
    

    
    @Test
    @TestOrder(22)
    public void test21_DocumentStructure() throws Exception {
        log("test21_DocumentStructure");

        CSVResource resource = getCSVResource("simple.csv");
        CSVDocument document = resource.loadResourceData();

        assertNotNull("Document should not be null", document);

        
        assertTrue("Document should have rows", document.getRowCount() > 0);
        assertNotNull("Rows list should not be null", document.getRows());

        
        assertTrue("Document should have columns", document.getColumnCount() > 0);
        assertNotNull("Columns list should not be null", document.getColumns());

        logger.info("Document structure: " + document.getRowCount() +
                " rows × " + document.getColumnCount() + " columns");
    }

    
    @Test
    @TestOrder(23)
    public void test22_RowAndCellAccess() throws Exception {
        log("test22_RowAndCellAccess");

        CSVResource resource = getCSVResource("simple.csv");
        CSVDocument document = resource.loadResourceData();

        if (document.getRowCount() > 0) {
            
            CSVRow row = document.getRowAt(0);
            assertNotNull("Row should not be null", row);

            
            assertEquals("Row should reference document", document, row.getCSVDocument());

            if (row.getCellCount() > 0) {
                
                CSVCell cell = row.getCellAt(0);
                assertNotNull("Cell should not be null", cell);

                
                assertEquals("Cell should reference row", row, cell.getCSVRow());

                logger.info("Row and cell relationships correct");
                logger.info("  First cell value: " + cell.getValue());
            }
        }
    }

    
    @Test
    @TestOrder(24)
    public void test23_InvalidAccess() throws Exception {
        log("test23_InvalidAccess");

        CSVResource resource = getCSVResource("simple.csv");
        CSVDocument document = resource.loadResourceData();

        
        assertNull("Invalid row index should return null",
                document.getRowAt(-1));
        assertNull("Out of bounds row should return null",
                document.getRowAt(9999));

        if (document.getRowCount() > 0) {
            CSVRow row = document.getRowAt(0);

            
            assertNull("Invalid cell index should return null",
                    row.getCellAt(-1));
            assertNull("Out of bounds cell should return null",
                    row.getCellAt(9999));
        }

        logger.info("Invalid access handled gracefully");
    }

    
    
    

    
    @Test
    @TestOrder(25)
    public void test24_CompleteLifecycle() throws Exception {
        log("test24_CompleteLifecycle");

        
        CSVResource resource = getCSVResource("Test1.csv");
        assertNotNull("Resource retrieved", resource);
        boolean isiy = resource.isLoaded();
        assertFalse("Resource not loaded", resource.isLoaded());

        
        CSVDocument document = resource.loadResourceData();
        assertNotNull("Document loaded", document);
        assertTrue("Resource loaded", resource.isLoaded());
        int initialRowCount = document.getRowCount();

        
        CSVRow newRow = resource.getFactory().makeCSVRow(document.getRowCount());
        newRow.setCSVDocument(document);
        CSVCell cell = resource.getFactory().makeCSVCell("TestValue", 0);
        cell.setCSVRow(newRow);
        newRow.addToCells(cell);
        document.addToRows(newRow);
        assertEquals("Row added", initialRowCount + 1, document.getRowCount());

        
        resource.unloadResourceData(false);
        assertFalse("Resource unloaded", resource.isLoaded());

        
        CSVDocument reloaded = resource.loadResourceData();
        assertNotNull("Document reloaded", reloaded);
        assertEquals("Data reset to original", initialRowCount, reloaded.getRowCount());

        logger.info("Complete lifecycle: GET → LOAD → MODIFY → UNLOAD → RELOAD");
    }

  
}
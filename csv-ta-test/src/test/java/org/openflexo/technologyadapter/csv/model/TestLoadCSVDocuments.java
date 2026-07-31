

package org.openflexo.technologyadapter.csv.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.ta.csv.AbstractTestCSV;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVResourceRepository;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


@RunWith(OrderedRunner.class)
public class TestLoadCSVDocuments extends AbstractTestCSV {

	private static CSVTechnologyAdapter csvTechnologyAdapter;
	private static CSVResourceRepository<?> csvResourceRepository;
	
	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		instanciateTestServiceManager(CSVTechnologyAdapter.class);


		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://www.openflexo.org/test/csv");

		assertNotNull("Resource center should not be null", resourceCenter);

		csvTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(CSVTechnologyAdapter.class);

		assertNotNull("CSV Technology Adapter should not be null", csvTechnologyAdapter);

		csvResourceRepository = csvTechnologyAdapter.getCSVResourceRepository(resourceCenter);

		assertNotNull("CSV Resource Repository should not be null", csvResourceRepository);

		logger.info("=== Available CSV Resources ===");
		for (CSVResource r : csvResourceRepository.getAllResources()) {
			logger.info("Resource: " + r.getName() + " - URI: " + r.getURI());
		}
	}

	@Test
	@TestOrder(2)
	public void testLoadSimpleCSV() {
		logTestSection("TEST: Load Simple CSV");

		CSVDocument document = getDocument("simple.csv");
		assertNotNull("Document should not be null", document);


		assertTrue("Document should have at least one row", document.getRowCount() > 0);
		logger.info("Document has " + document.getRowCount() + " rows");


		assertNotNull("Document should have resource", document.getResource());
		assertTrue("Resource should be CSVResource", document.getResource() instanceof CSVResource);

		logger.info("Simple CSV loaded successfully");
	}

	@Test
	@TestOrder(3)
	public void testLoadCSVWithHeaders() {
		logTestSection("TEST: Load CSV with Headers");

		CSVDocument document = getDocument("with_headers.csv");
		assertNotNull("Document should not be null", document);


		assertTrue("Document should have at least 2 rows (header + data)", document.getRowCount() >= 2);


		CSVRow headerRow = document.getRowAt(0);
		assertNotNull("Header row should not be null", headerRow);
		assertTrue("Header row should have cells", headerRow.getCellCount() > 0);


		logger.info("Header row has " + headerRow.getCellCount() + " columns:");
		for (int i = 0; i < headerRow.getCellCount(); i++) {
			CSVCell cell = headerRow.getCellAt(i);
			if (cell != null) {
				logger.info("  Column " + i + ": " + cell.getValue());
			}
		}


		if (document.getRowCount() > 1) {
			CSVRow dataRow = document.getRowAt(1);
			assertNotNull("Data row should not be null", dataRow);
			assertTrue("Data row should have cells", dataRow.getCellCount() > 0);
		}

		logger.info("CSV with headers loaded successfully");
	}

	@Test
	@TestOrder(4)
	public void testLoadCSVWithVariousTypes() {
		logTestSection("TEST: Load CSV with Various Data Types");

		CSVDocument document = getDocument("various_types.csv");
		assertNotNull("Document should not be null", document);


		assertTrue("Document should have at least 2 rows", document.getRowCount() >= 2);


		if (document.getRowCount() > 1) {
			CSVRow row = document.getRowAt(1);
			assertNotNull("Data row should not be null", row);
			assertTrue("Row should have multiple cells", row.getCellCount() >= 3);


			for (int i = 0; i < row.getCellCount(); i++) {
				CSVCell cell = row.getCellAt(i);
				assertNotNull("Cell " + i + " should not be null", cell);
				assertNotNull("Cell " + i + " value should not be null", cell.getValue());
				logger.info("Cell " + i + " value: " + cell.getValue());
			}
		}

		logger.info("CSV with various types loaded successfully");
	}

	@Test
	@TestOrder(5)
	public void testLoadCSVWithQuotes() {
		logTestSection("TEST: Load CSV with Quoted Values");

		CSVDocument document = getDocument("with_quotes.csv");
		assertNotNull("Document should not be null", document);


		assertTrue("Document should have rows", document.getRowCount() > 0);


		if (document.getRowCount() > 1) {
			CSVRow row = document.getRowAt(1);
			assertNotNull("Row should not be null", row);

			if (row.getCellCount() > 1) {
				CSVCell descCell = row.getCellAt(1);
				assertNotNull("Description cell should not be null", descCell);
				assertNotNull("Description value should not be null", descCell.getValue());
				logger.info("Quoted value: " + descCell.getValue());


				assertFalse("Value should not start with quote", descCell.getValue().startsWith("\""));
			}
		}

		logger.info("CSV with quoted values loaded successfully");
	}

	@Test
	@TestOrder(6)
	public void testLoadCSVWithEmptyCells() {
		logTestSection("TEST: Load CSV with Empty Cells");

		CSVDocument document = getDocument("with_empty_cells.csv");
		assertNotNull("Document should not be null", document);

		assertTrue("Document should have rows", document.getRowCount() > 0);


		if (document.getRowCount() > 1) {
			CSVRow row = document.getRowAt(1);
			assertNotNull("Row should not be null", row);


			for (int i = 0; i < row.getCellCount(); i++) {
				CSVCell cell = row.getCellAt(i);
				if (cell != null) {
					String value = cell.getValue();
					logger.info("Cell " + i + " value: '" + (value == null ? "null" : value) + "'");
				}
			}
		}

		logger.info("CSV with empty cells loaded successfully");
	}

	@Test
	@TestOrder(7)
	public void testCSVDocumentStructure() {
		logTestSection("TEST: CSV Document Structure");

		CSVDocument document = getDocument("simple.csv");
		assertNotNull("Document should not be null", document);


		assertNotNull("Document should have rows list", document.getRows());
		assertNotNull("Document should have columns list", document.getColumns());
		assertTrue("Document row count should be non-negative", document.getRowCount() >= 0);
		assertTrue("Document column count should be non-negative", document.getColumnCount() >= 0);


		if (document.getRowCount() > 0) {
			CSVRow row = document.getRowAt(0);
			assertNotNull("Row should not be null", row);
			assertNotNull("Row should have cells list", row.getCells());
			assertTrue("Row cell count should be non-negative", row.getCellCount() >= 0);
			assertEquals("Row should reference document", document, row.getCSVDocument());


			if (row.getCellCount() > 0) {
				CSVCell cell = row.getCellAt(0);
				assertNotNull("Cell should not be null", cell);
				assertNotNull("Cell should have value (even if empty)", cell.getValue());
				assertEquals("Cell should reference row", row, cell.getCSVRow());
				assertTrue("Cell column index should be valid", cell.getColumnIndex() >= 0);
			}
		}

		logger.info("CSV document structure verified");
	}

	@Test
	@TestOrder(8)
	public void testLoadMultipleDocuments() {
		logTestSection("TEST: Load Multiple Documents");


		CSVDocument doc1 = getDocument("simple.csv");
		assertNotNull("Document 1 should not be null", doc1);


		CSVDocument doc2 = getDocument("with_headers.csv");
		assertNotNull("Document 2 should not be null", doc2);


		assertNotSame("Documents should be different instances", doc1, doc2);
		assertNotSame("Document resources should be different", doc1.getResource(), doc2.getResource());

		logger.info("Multiple documents loaded successfully");
	}

	@Test
	@TestOrder(9)
	public void testResourceMetadata() {
		logTestSection("TEST: Resource Metadata");

		CSVResource resource = getCSVResource("simple.csv");
		assertNotNull("Resource should not be null", resource);


		assertNotNull("Resource URI should not be null", resource.getURI());
		assertNotNull("Resource name should not be null", resource.getName());
		logger.info("Resource URI: " + resource.getURI());
		logger.info("Resource name: " + resource.getName());


		CSVDocument document = null;
		try {
			document = resource.getResourceData();
		} catch (Exception e) {
			fail("Should not throw exception when getting resource data: " + e.getMessage());
		}
		assertNotNull("Resource data should not be null", document);

		logger.info("Resource metadata verified");
	}

	@Test
	@TestOrder(10)
	public void testRowAccessMethods() {
		logTestSection("TEST: Row Access Methods");

		CSVDocument document = getDocument("simple.csv");
		assertNotNull("Document should not be null", document);

		int rowCount = document.getRowCount();
		logger.info("Document has " + rowCount + " data rows");


		boolean hasHeader = document.getHasHeader();
		CSVRow headerRow = document.getHeaderRow();

		if (hasHeader && headerRow != null) {
			logger.info("Document has header row");


		}


		if (rowCount > 0) {

			CSVRow firstRow = document.getRowAt(0);
			assertNotNull("First row should not be null", firstRow);


			assertEquals("First row index should be 0", 0, firstRow.getRowIndex());


			CSVRow lastRow = document.getRowAt(rowCount - 1);
			assertNotNull("Last row should not be null", lastRow);
			assertEquals("Last row index should match", rowCount - 1, lastRow.getRowIndex());


			logger.info("Verifying all " + rowCount + " data rows have correct indices...");
			for (int i = 0; i < rowCount; i++) {
				CSVRow row = document.getRowAt(i);
				assertNotNull("Row at position " + i + " should not be null", row);

				int expectedIndex = i;
				int actualIndex = row.getRowIndex();

				assertEquals("Row at position " + i + " should have index " + expectedIndex,
						expectedIndex, actualIndex);

				logger.fine("Row position " + i + " has correct index: " + actualIndex);
			}
			logger.info("All row indices verified successfully");
		}


		assertNull("Row at invalid index should be null", document.getRowAt(-1));
		assertNull("Row at out of bounds index should be null", document.getRowAt(rowCount + 10));

		logger.info("Row access methods work correctly");
	}

	@Test
	@TestOrder(11)
	public void testCellAccessMethods() {
		logTestSection("TEST: Cell Access Methods");

		CSVDocument document = getDocument("simple.csv");
		assertNotNull("Document should not be null", document);

		if (document.getRowCount() > 0) {
			CSVRow row = document.getRowAt(0);
			assertNotNull("Row should not be null", row);

			int cellCount = row.getCellCount();
			logger.info("Row has " + cellCount + " cells");


			if (cellCount > 0) {
				CSVCell firstCell = row.getCellAt(0);
				assertNotNull("First cell should not be null", firstCell);
				assertEquals("First cell column index should be 0", 0, firstCell.getColumnIndex());

				CSVCell lastCell = row.getCellAt(cellCount - 1);
				assertNotNull("Last cell should not be null", lastCell);
				assertEquals("Last cell column index should match", cellCount - 1, lastCell.getColumnIndex());
			}


			assertNull("Cell at invalid index should be null", row.getCellAt(-1));
			assertNull("Cell at out of bounds index should be null", row.getCellAt(cellCount + 10));
		}

		logger.info("Cell access methods work correctly");
	}

	
	protected void logTestSection(String message) {
		logger.info("========================================");
		logger.info(message);
		logger.info("========================================");
	}
}
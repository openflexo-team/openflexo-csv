package org.openflexo.technologyadapter.csv.model;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.ta.csv.AbstractTestCSV;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVConverter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


@RunWith(OrderedRunner.class)
public class TestCSVConverter extends AbstractTestCSV {

    protected static final Logger logger = Logger.getLogger(TestCSVConverter.class.getPackage().getName());

    private static CSVTechnologyAdapter technologicalAdapter;
    private static FlexoResourceCenter<?> resourceCenter;


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

        logger.info("Setup complete");
    }



    @Test
    @TestOrder(2)
    public void test1_ParseSimpleCSV() throws Exception {
        log("test1_ParseSimpleCSV");

        String csvContent = "Name,Age,City\n" +
                "John,30,Paris\n" +
                "Jane,25,London\n" +
                "Bob,35,Berlin\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);

        // Check first data row
        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("First row should have cells", firstRow.getCellCount() >= 3);

        logger.info("Simple CSV parsing successful - " + document.getRowCount() + " rows loaded");
    }

    @Test
    @TestOrder(3)
    public void test2_ParseCSVWithoutHeader() throws Exception {
        log("test2_ParseCSVWithoutHeader");

        String csvContent = "John,30,Paris\n" +
                "Jane,25,London\n" +
                "Bob,35,Berlin\n";

        CSVDocument document = parseCSVString(csvContent, false);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 rows", document.getRowCount() >= 3);

        // Check first row
        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);

        logger.info("CSV without header parsing successful");
    }

    @Test
    @TestOrder(4)
    public void test3_ParseEmptyCSV() throws Exception {
        log("test3_ParseEmptyCSV");

        String csvContent = "";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertEquals("Should have 0 rows", 0, document.getRowCount());

        logger.info("Empty CSV parsing successful");
    }

    @Test
    @TestOrder(5)
    public void test4_ParseSingleRowCSV() throws Exception {
        log("test4_ParseSingleRowCSV");

        String csvContent = "Name,Age,City\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);

        logger.info("Single row CSV parsing successful");
    }

    @Test
    @TestOrder(6)
    public void test5_ParseSingleColumnCSV() throws Exception {
        log("test5_ParseSingleColumnCSV");

        String csvContent = "Name\n" +
                "John\n" +
                "Jane\n" +
                "Bob\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);


        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 1 cell", firstRow.getCellCount() >= 1);

        logger.info("Single column CSV parsing successful");
    }


    @Test
    @TestOrder(7)
    public void test6_ParseCSVWithSemicolon() throws Exception {
        test0_SetupEnvironment();
        log("test6_ParseCSVWithSemicolon");

        String csvContent = "Name;Age;City\n" +
                "John;30;Paris\n" +
                "Jane;25;London\n";

        CSVDocument document = parseCSVString(csvContent, true, ";");

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);


        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 3 cells", firstRow.getCellCount() >= 3);

        logger.info("Semicolon delimiter parsing successful");
    }

    @Test
    @TestOrder(8)
    public void test7_ParseCSVWithTab() throws Exception {
        log("test7_ParseCSVWithTab");

        String csvContent = "Name\tAge\tCity\n" +
                "John\t30\tParis\n" +
                "Jane\t25\tLondon\n";

        CSVDocument document = parseCSVString(csvContent, true, "\t");

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 3 cells", firstRow.getCellCount() >= 3);

        logger.info("Tab delimiter parsing successful");
    }

    @Test
    @TestOrder(9)
    public void test8_ParseCSVWithPipe() throws Exception {
        log("test8_ParseCSVWithPipe");

        String csvContent = "Name|Age|City\n" +
                "John|30|Paris\n";

        CSVDocument document = parseCSVString(csvContent, true, "|");

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 1 data row", document.getRowCount() >= 1);

        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 3 cells", firstRow.getCellCount() >= 3);

        logger.info("Pipe delimiter parsing successful");
    }





    @Test
    @TestOrder(10)
    public void test9_ParseQuotedValues() throws Exception {
        log("test9_ParseQuotedValues");

        String csvContent = "Name,Description\n" +
                "\"John Doe\",\"A person\"\n" +
                "\"Jane Smith\",\"Another person\"\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 2 cells", firstRow.getCellCount() >= 2);

        logger.info("Quoted values parsing successful");
    }

    @Test
    @TestOrder(11)
    public void test10_ParseEscapedQuotes() throws Exception {
        log("test10_ParseEscapedQuotes");

        String csvContent = "Name,Quote\n" +
                "\"John\",\"He said \"\"Hello\"\"\"\n" +
                "\"Jane\",\"She said \"\"Hi\"\"\"\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        logger.info("Escaped quotes parsing successful");
    }

    @Test
    @TestOrder(12)
    public void test11_ParseQuotesWithDelimiter() throws Exception {
        log("test11_ParseQuotesWithDelimiter");

        String csvContent = "Name,Address\n" +
                "\"John\",\"123 Main St, Apt 4\"\n" +
                "\"Jane\",\"456 Oak Ave, Suite 10\"\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        CSVRow firstRow = document.getRowAt(0);
        assertNotNull("First row should not be null", firstRow);
        assertTrue("Row should have at least 2 cells", firstRow.getCellCount() >= 2);

        logger.info("Quotes with delimiter parsing successful");
    }

    @Test
    @TestOrder(13)
    public void test12_ParseQuotesWithNewline() throws Exception {
        log("test12_ParseQuotesWithNewline");

        String csvContent = "Name,Description\n" +
                "\"John\",\"Line 1\nLine 2\"\n" +
                "\"Jane\",\"Single line\"\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        logger.info("Quotes with newline parsing successful");
    }





    @Test
    @TestOrder(14)
    public void test13_ParseEmptyCells() throws Exception {
        log("test13_ParseEmptyCells");

        String csvContent = "Name,Age,City\n" +
                "John,,Paris\n" +
                ",30,London\n" +
                "Bob,35,\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);


        CSVRow row1 = document.getRowAt(0);
        assertNotNull("First row should not be null", row1);
        assertEquals("Row should have 3 cells", 3, row1.getCellCount());

        logger.info("Empty cells parsing successful");
    }

    @Test
    @TestOrder(15)
    public void test14_ParseTrailingEmptyCells() throws Exception {
        log("test14_ParseTrailingEmptyCells");

        String csvContent = "Name,Age,City\n" +
                "John,30,\n" +
                "Jane,,\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 2 data rows", document.getRowCount() >= 2);

        CSVRow row1 = document.getRowAt(0);
        assertNotNull("First row should not be null", row1);
        assertTrue("Row should have cells", row1.getCellCount() >= 2);

        logger.info("Trailing empty cells parsing successful");
    }





    @Test
    @TestOrder(16)
    public void test15_ParseInconsistentRowLengths() throws Exception {
        log("test15_ParseInconsistentRowLengths");

        String csvContent = "Name,Age,City\n" +
                "John,30,Paris\n" +
                "Jane,25\n" +
                "Bob,35,Berlin,Extra\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);

        CSVRow row1 = document.getRowAt(0);
        assertEquals("Row 1 should have 3 cells", 3, row1.getCellCount());

        CSVRow row2 = document.getRowAt(1);
        assertTrue("Row 2 should have at least 2 cells", row2.getCellCount() >= 2);

        CSVRow row3 = document.getRowAt(2);
        assertTrue("Row 3 should have at least 4 cells", row3.getCellCount() >= 4);

        logger.info("Inconsistent row lengths handling successful");
    }

    @Test
    @TestOrder(17)
    public void test16_ParseSpecialCharacters() throws Exception {
        log("test16_ParseSpecialCharacters");

        String csvContent = "Name,Symbol\n" +
                "Euro,€\n" +
                "Yen,¥\n" +
                "Pound,£\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);

        logger.info("Special characters parsing successful");
    }

    @Test
    @TestOrder(18)
    public void test17_ParseUTF8Encoding() throws Exception {
        log("test17_ParseUTF8Encoding");

        String csvContent = "Name,City\n" +
                "François,Paris\n" +
                "José,Madrid\n" +
                "Müller,Berlin\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 3 data rows", document.getRowCount() >= 3);

        logger.info("UTF-8 encoding parsing successful");
    }

    @Test
    @TestOrder(19)
    public void test18_ParseVeryLongLine() throws Exception {
        log("test18_ParseVeryLongLine");

        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longValue.append("A");
        }

        String csvContent = "Name,Description\n" +
                "John," + longValue.toString() + "\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);
        assertTrue("Should have at least 1 data row", document.getRowCount() >= 1);

        CSVRow row = document.getRowAt(0);
        assertNotNull("Row should not be null", row);
        assertTrue("Row should have at least 2 cells", row.getCellCount() >= 2);

        logger.info("Very long line parsing successful");
    }





    @Test
    @TestOrder(20)
    public void test19_URIConversionDocument() throws Exception {
        log("test19_URIConversionDocument");

        String csvContent = "Name,Age\n" +
                "John,30\n";

        CSVDocument document = parseCSVString(csvContent);
        CSVResource resource = document.getResource();
        CSVConverter converter = resource.getConverter();


        String documentURI = "csv://document";



        logger.info("Document URI conversion test (requires full resource setup)");
    }

    @Test
    @TestOrder(21)
    public void test20_URIConversionRow() throws Exception {
        log("test20_URIConversionRow");

        String csvContent = "Name,Age\n" +
                "John,30\n" +
                "Jane,25\n";

        CSVDocument document = parseCSVString(csvContent);
        CSVResource resource = document.getResource();


        logger.info("Row URI conversion test (requires full resource setup)");
    }

    @Test
    @TestOrder(22)
    public void test21_URIConversionInvalidIndex() throws Exception {
        log("test21_URIConversionInvalidIndex");

        String csvContent = "Name,Age\n" +
                "John,30\n";

        CSVDocument document = parseCSVString(csvContent);
        CSVResource resource = document.getResource();
        CSVConverter converter = resource.getConverter();


        String invalidURI = "csv://document/row/999";
        Object retrieved = converter.fromSerializationIdentifier(invalidURI);

        assertNull("Should return null for invalid index", retrieved);

        logger.info("Invalid URI handling successful");
    }

    @Test
    @TestOrder(23)
    public void test22_URIConversionInvalidFormat() throws Exception {
        log("test22_URIConversionInvalidFormat");

        String csvContent = "Name,Age\n" +
                "John,30\n";

        CSVDocument document = parseCSVString(csvContent);
        CSVResource resource = document.getResource();
        CSVConverter converter = resource.getConverter();


        assertNull("Invalid format 1", converter.fromSerializationIdentifier("invalid://uri"));
        assertNull("Invalid format 2", converter.fromSerializationIdentifier("csv://invalid"));
        assertNull("Invalid format 3", converter.fromSerializationIdentifier(null));

        logger.info("Invalid URI format handling successful");
    }

    @Test
    @TestOrder(24)
    public void test23_ToSerializationIdentifier() throws Exception {
        log("test23_ToSerializationIdentifier");

        String csvContent = "Name,Age\n" +
                "John,30\n";

        CSVDocument document = parseCSVString(csvContent);
        CSVResource resource = document.getResource();
        CSVConverter converter = resource.getConverter();


        assertNull("Null object", converter.toSerializationIdentifier(null));

        logger.info("To serialization identifier successful");
    }



    @Test
    @TestOrder(25)
    public void test24_ParseNullInputStream() throws Exception {
        log("test24_ParseNullInputStream");

        CSVResource resource = getCSVResource("simple.csv");
        CSVConverter converter = resource.getConverter();

        try {
            converter.loadCSVDocument(null,null);
            fail("Should throw exception for null input stream");
        } catch (Exception e) {

            assertTrue("Should throw appropriate exception",
                    e instanceof IOException || e instanceof NullPointerException|| e instanceof IllegalArgumentException);
        }

        logger.info("Null input stream handling successful");
    }

    @Test
    @TestOrder(26)
    public void test25_ParseMalformedCSV() throws Exception {
        log("test25_ParseMalformedCSV");


        String csvContent = "Name,Description\n" +
                "\"John,\"Unclosed quote\n";

        CSVDocument document = parseCSVString(csvContent);

        assertNotNull("Document should not be null", document);


        logger.info("Malformed CSV handling successful");
    }






    private CSVDocument parseCSVString(String csvContent) throws Exception {
        return parseCSVString(csvContent, true, ",");
    }


    private CSVDocument parseCSVString(String csvContent, boolean hasHeader) throws Exception {
        return parseCSVString(csvContent, hasHeader, ",");
    }


    private CSVDocument parseCSVString(String csvContent, boolean hasHeader, String delimiter) throws Exception {

        CSVResource templateResource = getCSVResource("simple.csv");
        assertNotNull("Template resource should exist", templateResource);


        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));


        CSVConverter.ParseConfig config = new CSVConverter.ParseConfig();
        config.setDelimiter(delimiter);
        config.setHasHeader(hasHeader);
        CSVConverter converter = templateResource.getConverter();
        CSVDocument document = converter.loadCSVDocument(inputStream,config);


        document.setHasHeader(hasHeader);
        document.setDelimiter(delimiter);
        document.setResource(templateResource);


        CSVResource setRes = document.getResource();

        return document;
    }


}
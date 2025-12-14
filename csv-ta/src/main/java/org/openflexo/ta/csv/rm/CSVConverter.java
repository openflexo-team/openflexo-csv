/**
 *
 * Copyright (c) 2018, Openflexo
 *
 * This file is part of OpenflexoTechnologyAdapter, a component of the software infrastructure
 * developed at Openflexo.
 *
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.ta.csv.rm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVColumn;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVModelFactory;
import org.openflexo.ta.csv.model.CSVObject;
import org.openflexo.ta.csv.model.CSVRow;

public class CSVConverter {


    public static class ParseConfig {
        private String delimiter = ",";
        private boolean skipEmptyLines = true;
        private boolean dynamicTyping = false;
        private List<String> delimitersToGuess = Arrays.asList(",", "\t", "|", ";");
        private boolean hasHeader = true;
        public String getDelimiter() { return delimiter; }
        public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
        public boolean isSkipEmptyLines() { return skipEmptyLines; }
        public void setSkipEmptyLines(boolean skip) { this.skipEmptyLines = skip; }
        public boolean isDynamicTyping() { return dynamicTyping; }
        public void setDynamicTyping(boolean dynamic) { this.dynamicTyping = dynamic; }
        public List<String> getDelimitersToGuess() { return delimitersToGuess; }
        public void setDelimitersToGuess(List<String> delimiters) {
            this.delimitersToGuess = delimiters;
        }

        public boolean isHasHeader() {
            return hasHeader;
        }

        public void setHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }
    }

    private static final Logger logger = Logger.getLogger(CSVConverter.class.getPackage().getName());


    private final CSVResource resource;


    public CSVConverter(CSVResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        this.resource = resource;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Created CSV converter for resource: " + resource.getURI());
        }
    }




    public CSVObject fromSerializationIdentifier(String serializationIdentifier) {
        if (serializationIdentifier == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Cannot deserialize null identifier");
            }
            return null;
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deserializing CSV object from URI: " + serializationIdentifier);
        }

        CSVDocument document = null;
        try {
            document = resource.getResourceData();
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Error getting CSV document: " + e.getMessage());
            }
            return null;
        }

        if (document == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("CSV document is null, cannot deserialize: " + serializationIdentifier);
            }
            return null;
        }

        CSVObject result = parseURI(document, serializationIdentifier);
        if (result != null && logger.isLoggable(Level.FINE)) {
            logger.fine("Successfully deserialized " + result.getClass().getSimpleName() + " from URI");
        }
        return result;
    }


    public String toSerializationIdentifier(CSVObject object) {
        if (object == null) {
            return null;
        }
        String identifier = object.getSerializationIdentifier();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Serialized " + object.getClass().getSimpleName() + " to URI: " + identifier);
        }
        return identifier;
    }


    private CSVObject parseURI(CSVDocument document, String uri) {
        if (uri == null || !uri.startsWith("csv://")) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Invalid CSV URI format: " + uri);
            }
            return null;
        }

        String path = uri.substring(6);

        if (path.equals("document") || path.isEmpty()) {
            return document;
        }

        String[] parts = path.split("/");

        if (parts.length < 1 || !"document".equals(parts[0])) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("URI must start with 'document': " + uri);
            }
            return null;
        }

        try {
            if (parts.length >= 3 && "row".equals(parts[1])) {
                return parseRowURI(document, parts, uri);
            }

            if (parts.length >= 3 && "column".equals(parts[1])) {
                return parseColumnURI(document, parts, uri);
            }

        } catch (NumberFormatException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Invalid index in URI: " + uri + " - " + e.getMessage());
            }
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Error parsing URI: " + uri + " - " + e.getMessage());
            }
        }

        if (logger.isLoggable(Level.WARNING)) {
            logger.warning("Could not parse CSV URI: " + uri);
        }
        return null;
    }

    private CSVObject parseRowURI(CSVDocument document, String[] parts, String fullUri) {
        int rowIndex = Integer.parseInt(parts[2]);

        if (rowIndex < 0 || rowIndex >= document.getRowCount()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Row index out of bounds: " + rowIndex + " (document has " + document.getRowCount() + " rows)");
            }
            return null;
        }

        CSVRow row = document.getRowAt(rowIndex);
        if (row == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Row is null at index: " + rowIndex);
            }
            return null;
        }

        if (parts.length >= 5 && "cell".equals(parts[3])) {
            int cellIndex = Integer.parseInt(parts[4]);

            if (cellIndex < 0 || cellIndex >= row.getCellCount()) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Cell index out of bounds: " + cellIndex + " (row has " + row.getCellCount() + " cells)");
                }
                return null;
            }

            CSVCell cell = row.getCellAt(cellIndex);
            if (cell == null) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("Cell is null at row " + rowIndex + ", column " + cellIndex);
                }
            }
            return cell;
        }

        return row;
    }


    private CSVObject parseColumnURI(CSVDocument document, String[] parts, String fullUri) {
        int columnIndex = Integer.parseInt(parts[2]);

        if (columnIndex < 0 || columnIndex >= document.getColumnCount()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Column index out of bounds: " + columnIndex + " (document has " + document.getColumnCount() + " columns)");
            }
            return null;
        }

        CSVColumn column = document.getColumnAt(columnIndex);
        if (column == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Column is null at index: " + columnIndex);
            }
        }
        return column;
    }


    public CSVDocument loadCSVDocument(InputStream inputStream,ParseConfig parseConfig) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.info("Loading CSV document from stream");
        }

        CSVModelFactory factory = resource.getFactory();
        if (factory == null) {
            throw new IllegalStateException("Model factory is null");
        }

        CSVDocument document = factory.makeCSVDocument();
        document.setResource(resource);
        String delimiter = parseConfig.getDelimiter(); // Default is ","
        boolean hasHeader = parseConfig.isHasHeader(); // Default is true
        String encoding = "UTF-8"; // Default encoding

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("CSV configuration: delimiter='" + delimiter + "', hasHeader=" + hasHeader + ", encoding=" + encoding);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            CSVRow headerRow = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("Skipping empty line " + lineNumber);
                    }
                    continue;
                }

                String[] values = parseLine(line, delimiter);

                if (lineNumber == 1 && hasHeader) {
                    headerRow = createHeaderRow(document, factory, values);
                    document.setHeaderRow(headerRow);

                    createColumns(document, factory, values);

                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Created header row with " + values.length + " columns");
                    }
                } else {
                    int rowIndex = hasHeader ? lineNumber - 2 : lineNumber - 1;
                    CSVRow row = createDataRow(document, factory, values, rowIndex);
                    document.addToRows(row);

                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("Created row " + rowIndex + " with " + values.length + " cells");
                    }
                }
            }

            if (logger.isLoggable(Level.INFO)) {
                logger.info("Successfully loaded CSV: " + document.getRowCount() + " rows, " + document.getColumnCount() + " columns");
            }
        }

        return document;
    }

    private CSVRow createHeaderRow(CSVDocument document, CSVModelFactory factory, String[] values) {
        CSVRow headerRow = factory.makeCSVRow(-1);
        headerRow.setCSVDocument(document);

        for (int i = 0; i < values.length; i++) {
            CSVCell cell = factory.makeCSVCell(values[i], i);
            cell.setCSVRow(headerRow);
            headerRow.addToCells(cell);
        }

        return headerRow;
    }

    private void createColumns(CSVDocument document, CSVModelFactory factory, String[] headerValues) {
        for (int i = 0; i < headerValues.length; i++) {
            CSVColumn column = factory.makeCSVColumn(headerValues[i], i);
            column.setCSVDocument(document);
            document.addToColumns(column);
        }
    }
    private CSVRow createDataRow(CSVDocument document, CSVModelFactory factory, String[] values, int rowIndex) {
        CSVRow row = factory.makeCSVRow(rowIndex);
        row.setCSVDocument(document);

        for (int i = 0; i < values.length; i++) {
            CSVCell cell = factory.makeCSVCell(values[i], i);
            cell.setCSVRow(row);
            row.addToCells(cell);
        }

        return row;
    }

    private String[] parseLine(String line, String delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && delimiter.length() == 1 && c == delimiter.charAt(0)) {
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else if (!inQuotes && delimiter.length() > 1 && line.regionMatches(i, delimiter, 0, delimiter.length())) {
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
                i += delimiter.length() - 1;
            } else {
                currentValue.append(c);
            }
        }

        // Add last field
        values.add(currentValue.toString());

        return values.toArray(new String[0]);
    }



    
    public CSVResource getResource() {
        return resource;
    }

    
    public void delete() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deleting CSV converter");
        }
    }
}
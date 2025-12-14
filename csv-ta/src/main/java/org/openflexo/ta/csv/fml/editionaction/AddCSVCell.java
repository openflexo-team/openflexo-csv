/**
 *
 * Copyright (c) 2018, Openflexo
 *
 * This file is part of CSVConnector, a component of the software infrastructure 
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

package org.openflexo.ta.csv.fml.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver.TechnologySpecificActionDefiningReceiverImpl;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.validation.BindingIsRequiredAndMustBeValid;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.csv.CSVModelSlot;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;


@ModelEntity
@ImplementationClass(AddCSVCell.AddCSVCellImpl.class)
@XMLElement
@FML("AddCSVCell")
public interface AddCSVCell extends CSVAction<CSVCell> {

    @PropertyIdentifier(type = DataBinding.class)
    public static final String ROW_KEY = "row";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String VALUE_KEY = "value";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String COLUMN_INDEX_KEY = "columnIndex";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String ROW_INDEX_KEY = "rowIndex";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String CSV_DOCUMENT_KEY = "csvDocument";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String CELL_TO_COPY_KEY = "cellToCopy";

    @PropertyIdentifier(type = boolean.class)
    public static final String IS_ROW_INDEX_KEY = "isRowIndex";

    
    @Getter(value = ROW_KEY)
    @XMLAttribute
    @FMLAttribute(value = ROW_KEY)
    public DataBinding<CSVRow> getRow();

    @Setter(ROW_KEY)
    public void setRow(DataBinding<CSVRow> row);

    
    @Getter(value = VALUE_KEY)
    @XMLAttribute
    @FMLAttribute(value = VALUE_KEY)
    public DataBinding<Object> getValue();

    @Setter(VALUE_KEY)
    public void setValue(DataBinding<Object> value);

    
    @Getter(value = COLUMN_INDEX_KEY)
    @XMLAttribute
    @FMLAttribute(value = COLUMN_INDEX_KEY)
    public DataBinding<Integer> getColumnIndex();

    @Setter(COLUMN_INDEX_KEY)
    public void setColumnIndex(DataBinding<Integer> columnIndex);

    
    @Getter(value = ROW_INDEX_KEY)
    @XMLAttribute
    @FMLAttribute(value = ROW_INDEX_KEY)
    public DataBinding<Integer> getRowIndex();

    @Setter(ROW_INDEX_KEY)
    public void setRowIndex(DataBinding<Integer> rowIndex);

    
    @Getter(value = CSV_DOCUMENT_KEY)
    @XMLAttribute
    @FMLAttribute(value = CSV_DOCUMENT_KEY)
    public DataBinding<CSVDocument> getCSVDocument();

    @Setter(CSV_DOCUMENT_KEY)
    public void setCSVDocument(DataBinding<CSVDocument> csvDocument);

    
    @Getter(value = IS_ROW_INDEX_KEY, defaultValue = "false")
    @XMLAttribute
    public boolean isRowIndex();

    @Setter(IS_ROW_INDEX_KEY)
    public void setRowIndex(boolean isRowIndex);

    
    @Getter(value = CELL_TO_COPY_KEY)
    @XMLAttribute
    public DataBinding<CSVCell> getCellToCopy();

    @Setter(CELL_TO_COPY_KEY)
    public void setCellToCopy(DataBinding<CSVCell> cellToCopy);

    
    public static abstract class AddCSVCellImpl
            extends TechnologySpecificActionDefiningReceiverImpl<CSVModelSlot, CSVDocument, CSVCell> implements AddCSVCell {

        private static final Logger logger = Logger.getLogger(AddCSVCell.class.getPackage().getName());

        private DataBinding<Object> value;
        private DataBinding<Integer> columnIndex;
        private DataBinding<Integer> rowIndex;
        private DataBinding<CSVRow> row;
        private DataBinding<CSVDocument> csvDocument;
        private DataBinding<CSVCell> cellToCopy;

        public AddCSVCellImpl() {
            super();
        }

        @Override
        public Type getAssignableType() {
            return CSVCell.class;
        }

        @Override
        public CSVCell execute(RunTimeEvaluationContext evaluationContext) {

            CSVCell csvCell = null;

            try {
                CSVRow csvRow = null;

                // Get the row - either by index or direct binding
                if (isRowIndex()) {
                    Integer rowIndex = getRowIndex().getBindingValue(evaluationContext);
                    CSVDocument document = getCSVDocument().getBindingValue(evaluationContext);

                    if (document != null && rowIndex != null) {
                        csvRow = document.getRowAt(rowIndex);

                        if (csvRow == null) {
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.warning("Row not found at index " + rowIndex);
                            }
                        }
                    }
                    else if (document == null) {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.warning("Cannot add cell: CSV document is null");
                        }
                    }
                    else if (rowIndex == null) {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.warning("Cannot add cell: row index is null");
                        }
                    }
                }
                else {
                    csvRow = getRow().getBindingValue(evaluationContext);

                    if (csvRow == null) {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.warning("Cannot add cell: row is null");
                        }
                    }
                }

                if (csvRow == null) {
                    return null;
                }

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Adding cell to row at index " + csvRow.getRowIndex());
                }

                // Get column index
                Integer columnIndex = getColumnIndex().getBindingValue(evaluationContext);

                if (columnIndex == null) {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.warning("Cannot add cell: column index is null");
                    }
                    return null;
                }

                if (columnIndex < 0) {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.warning("Cannot add cell: column index must be non-negative, got " + columnIndex);
                    }
                    return null;
                }

                // Check if cell already exists at this column
                csvCell = csvRow.getCellAt(columnIndex);

                if (csvCell != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Cell already exists at column " + columnIndex + ", updating value");
                    }
                }
                else {
                    // Create new cell
                    Object value = getValue().getBindingValue(evaluationContext);
                    String cellValue = (value != null) ? value.toString() : "";

                    csvCell = csvRow.getCSVDocument().getResource().getFactory().makeCSVCell(cellValue, columnIndex);

                    if (csvCell == null) {
                        if (logger.isLoggable(Level.SEVERE)) {
                            logger.severe("Failed to create CSVCell at column " + columnIndex);
                        }
                        return null;
                    }

                    // Set bidirectional relationships
                    csvCell.setCSVRow(csvRow);
                    csvRow.addToCells(csvCell);

                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("Created new CSVCell at row " + csvRow.getRowIndex() + ", column " + columnIndex);
                    }
                }

                // Copy from another cell if specified
                if (getCellToCopy() != null && getCellToCopy().isValid()) {
                    CSVCell cellToCopy = getCellToCopy().getBindingValue(evaluationContext);

                    if (cellToCopy != null) {
                        csvCell.setValue(cellToCopy.getValue());

                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Copied value from cell: " + cellToCopy.getValue());
                        }
                    }
                }

                // Set value if not copying
                if (getValue() != null && getValue().isValid()) {
                    Object value = getValue().getBindingValue(evaluationContext);

                    if (value != null) {
                        csvCell.setValue(value.toString());

                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest("Set cell value to: " + value);
                        }
                    }
                    else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Value is null, cell will have empty value");
                        }
                    }
                }

                // Mark document as modified
                csvRow.getCSVDocument().getResource().setModified(true);

            } catch (TypeMismatchException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Type mismatch in AddCSVCell: " + e.getMessage());
                }
                e.printStackTrace();
            } catch (NullReferenceException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Null reference in AddCSVCell: " + e.getMessage());
                }
                e.printStackTrace();
            } catch (ReflectiveOperationException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Reflection error in AddCSVCell: " + e.getMessage());
                }
                e.printStackTrace();
            } catch (Exception e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Unexpected error in AddCSVCell: " + e.getMessage());
                }
                e.printStackTrace();
            }

            return csvCell;
        }

        // DataBinding implementations

        @Override
        public DataBinding<Object> getValue() {
            if (value == null) {
                value = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.GET);
                value.setBindingName("value");
            }
            return value;
        }

        @Override
        public void setValue(DataBinding<Object> value) {
            if (value != null) {
                value.setOwner(this);
                value.setDeclaredType(Object.class);
                value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                value.setBindingName("value");
            }
            this.value = value;
        }

        @Override
        public DataBinding<Integer> getRowIndex() {
            if (rowIndex == null) {
                rowIndex = new DataBinding<>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
                rowIndex.setBindingName("rowIndex");
            }
            return rowIndex;
        }

        @Override
        public void setRowIndex(DataBinding<Integer> rowIndex) {
            if (rowIndex != null) {
                rowIndex.setOwner(this);
                rowIndex.setDeclaredType(Integer.class);
                rowIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                rowIndex.setBindingName("rowIndex");
            }
            this.rowIndex = rowIndex;
        }

        @Override
        public DataBinding<Integer> getColumnIndex() {
            if (columnIndex == null) {
                columnIndex = new DataBinding<>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
                columnIndex.setBindingName("columnIndex");
            }
            return columnIndex;
        }

        @Override
        public void setColumnIndex(DataBinding<Integer> columnIndex) {
            if (columnIndex != null) {
                columnIndex.setOwner(this);
                columnIndex.setDeclaredType(Integer.class);
                columnIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                columnIndex.setBindingName("columnIndex");
            }
            this.columnIndex = columnIndex;
        }

        @Override
        public DataBinding<CSVRow> getRow() {
            if (row == null) {
                row = new DataBinding<>(this, CSVRow.class, DataBinding.BindingDefinitionType.GET);
                row.setBindingName("row");
            }
            return row;
        }

        @Override
        public void setRow(DataBinding<CSVRow> row) {
            if (row != null) {
                row.setOwner(this);
                row.setDeclaredType(CSVRow.class);
                row.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                row.setBindingName("row");
            }
            this.row = row;
        }

        @Override
        public DataBinding<CSVDocument> getCSVDocument() {
            if (csvDocument == null) {
                csvDocument = new DataBinding<>(this, CSVDocument.class, DataBinding.BindingDefinitionType.GET);
                csvDocument.setBindingName("csvDocument");
            }
            return csvDocument;
        }

        @Override
        public void setCSVDocument(DataBinding<CSVDocument> csvDocument) {
            if (csvDocument != null) {
                csvDocument.setOwner(this);
                csvDocument.setDeclaredType(CSVDocument.class);
                csvDocument.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                csvDocument.setBindingName("csvDocument");
            }
            this.csvDocument = csvDocument;
        }

        @Override
        public DataBinding<CSVCell> getCellToCopy() {
            if (cellToCopy == null) {
                cellToCopy = new DataBinding<>(this, CSVCell.class, DataBinding.BindingDefinitionType.GET);
                cellToCopy.setBindingName("cellToCopy");
            }
            return cellToCopy;
        }

        @Override
        public void setCellToCopy(DataBinding<CSVCell> cellToCopy) {
            if (cellToCopy != null) {
                cellToCopy.setOwner(this);
                cellToCopy.setDeclaredType(CSVCell.class);
                cellToCopy.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                cellToCopy.setBindingName("cellToCopy");
            }
            this.cellToCopy = cellToCopy;
        }
    }

    // Validation rules

    
    @DefineValidationRule
    public static class ColumnIndexBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddCSVCell> {
        public ColumnIndexBindingIsRequiredAndMustBeValid() {
            super("'columnIndex'_binding_is_required_and_must_be_valid", AddCSVCell.class);
        }

        @Override
        public DataBinding<Integer> getBinding(AddCSVCell object) {
            return object.getColumnIndex();
        }
    }
}
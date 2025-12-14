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
import java.util.List;
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
@ImplementationClass(AddCSVRow.AddCSVRowImpl.class)
@XMLElement
@FML("AddCSVRow")
public interface AddCSVRow extends CSVAction<CSVRow> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String CSV_DOCUMENT_KEY = "csvDocument";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String CSV_CELLS_KEY = "csvCells";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROW_INDEX_KEY = "rowIndex";

	
	@Getter(value = CSV_DOCUMENT_KEY)
	@XMLAttribute
	@FMLAttribute(value = CSV_DOCUMENT_KEY)
	public DataBinding<CSVDocument> getCSVDocument();

	@Setter(CSV_DOCUMENT_KEY)
	public void setCSVDocument(DataBinding<CSVDocument> csvDocument);

	
	@Getter(value = CSV_CELLS_KEY)
	@XMLAttribute
	@FMLAttribute(value = CSV_CELLS_KEY)
	public DataBinding<List<CSVCell>> getCSVCells();

	@Setter(CSV_CELLS_KEY)
	public void setCSVCells(DataBinding<List<CSVCell>> csvCells);

	
	@Getter(value = ROW_INDEX_KEY)
	@XMLAttribute
	@FMLAttribute(value = ROW_INDEX_KEY)
	public DataBinding<Integer> getRowIndex();

	@Setter(ROW_INDEX_KEY)
	public void setRowIndex(DataBinding<Integer> rowIndex);

	
	public static abstract class AddCSVRowImpl
			extends TechnologySpecificActionDefiningReceiverImpl<CSVModelSlot, CSVDocument, CSVRow> implements AddCSVRow {

		private static final Logger logger = Logger.getLogger(AddCSVRow.class.getPackage().getName());

		private DataBinding<List<CSVCell>> csvCells;
		private DataBinding<CSVDocument> csvDocument;
		private DataBinding<Integer> rowIndex;


		@Override
		public Type getAssignableType() {
			return CSVRow.class;
		}

		@Override
		public CSVRow execute(RunTimeEvaluationContext evaluationContext) {

			CSVRow csvRow = null;

			try {
				// Get CSV document from binding
				CSVDocument document = getCSVDocument().getBindingValue(evaluationContext);

				if (document == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Cannot add row: CSV document is null");
					}
					return null;
				}

				// Get row index from binding
				Integer rowIndex = getRowIndex().getBindingValue(evaluationContext);

				if (rowIndex == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Cannot add row: row index is null");
					}
					return null;
				}

				// Validate row index is non-negative
				if (rowIndex < 0) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Cannot add row: row index must be non-negative, got " + rowIndex);
					}
					return null;
				}

				// Check if row already exists at this index
				if (rowIndex < document.getRowCount()) {
					csvRow = document.getRowAt(rowIndex);
					if (csvRow != null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Row already exists at index " + rowIndex + ", returning existing row");
						}
					}
				}

				// Create new row if it doesn't exist
				if (csvRow == null) {
					csvRow = document.getResource().getFactory().makeCSVRow(rowIndex);

					if (csvRow == null) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.severe("Failed to create CSVRow at index " + rowIndex);
						}
						return null;
					}

					// Set bidirectional relationship
					csvRow.setCSVDocument(document);
					document.addToRows(csvRow);

					if (logger.isLoggable(Level.INFO)) {
						logger.info("Created new CSVRow at index " + rowIndex);
					}
				}

				// Add cells if provided
				if (getCSVCells() != null && getCSVCells().isValid()) {
					List<CSVCell> cells = getCSVCells().getBindingValue(evaluationContext);
					if (cells != null && !cells.isEmpty()) {
						for (CSVCell cell : cells) {
							if (cell != null) {
								csvRow.addToCells(cell);
								cell.setCSVRow(csvRow);
							}
						}

						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Added " + cells.size() + " cells to row " + rowIndex);
						}
					}
				}

				// Mark document as modified
				document.getResource().setModified(true);

				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Row " + rowIndex + " has " + csvRow.getCellCount() + " cells");
				}

			} catch (TypeMismatchException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Type mismatch in AddCSVRow: " + e.getMessage());
				}
				e.printStackTrace();
			} catch (NullReferenceException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Null reference in AddCSVRow: " + e.getMessage());
				}
				e.printStackTrace();
			} catch (ReflectiveOperationException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Reflection error in AddCSVRow: " + e.getMessage());
				}
				e.printStackTrace();
			} catch (Exception e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Unexpected error in AddCSVRow: " + e.getMessage());
				}
				e.printStackTrace();
			}

			return csvRow;
		}

		// DataBinding implementations

		@Override
		public DataBinding<List<CSVCell>> getCSVCells() {
			if (csvCells == null) {
				csvCells = new DataBinding<>(this, List.class, DataBinding.BindingDefinitionType.GET);
				csvCells.setBindingName("csvCells");
			}
			return csvCells;
		}

		@Override
		public void setCSVCells(DataBinding<List<CSVCell>> csvCells) {
			if (csvCells != null) {
				csvCells.setOwner(this);
				csvCells.setDeclaredType(List.class);
				csvCells.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				csvCells.setBindingName("csvCells");
			}
			this.csvCells = csvCells;
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
	}

	// Validation rules

	
	@DefineValidationRule
	public static class DocumentBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddCSVRow> {
		public DocumentBindingIsRequiredAndMustBeValid() {
			super("'csvDocument'_binding_is_required_and_must_be_valid", AddCSVRow.class);
		}

		@Override
		public DataBinding<CSVDocument> getBinding(AddCSVRow object) {
			return object.getCSVDocument();
		}
	}

	
	@DefineValidationRule
	public static class RowIndexBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddCSVRow> {
		public RowIndexBindingIsRequiredAndMustBeValid() {
			super("'rowIndex'_binding_is_required_and_must_be_valid", AddCSVRow.class);
		}

		@Override
		public DataBinding<Integer> getBinding(AddCSVRow object) {
			return object.getRowIndex();
		}
	}
}
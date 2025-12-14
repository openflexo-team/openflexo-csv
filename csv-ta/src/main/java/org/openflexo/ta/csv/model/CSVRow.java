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

package org.openflexo.ta.csv.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.pamela.annotations.*;
import org.openflexo.pamela.annotations.Getter.Cardinality;


@ModelEntity
@ImplementationClass(value = CSVRow.CSVRowImpl.class)
@XMLElement
public interface CSVRow extends CSVObject {


	@PropertyIdentifier(type = CSVDocument.class)
	public static final String CSV_DOCUMENT_KEY = "csvDocument";

	@PropertyIdentifier(type = CSVCell.class, cardinality = Cardinality.LIST)
	public static final String CELLS_KEY = "cells";

	@PropertyIdentifier(type = Integer.class)
	public static final String ROW_INDEX_KEY = "rowIndex";



	@Getter(value = CSV_DOCUMENT_KEY, inverse = CSVDocument.ROWS_KEY)
	public CSVDocument getCSVDocument();


	@Setter(CSV_DOCUMENT_KEY)
	public void setCSVDocument(CSVDocument document);




	@Getter(value = CELLS_KEY, cardinality = Cardinality.LIST,
			inverse = CSVCell.CSV_ROW_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(CloningStrategy.StrategyType.CLONE)
	public List<CSVCell> getCells();


	@Adder(CELLS_KEY)
	public void addToCells(CSVCell cell);




	@Remover(CELLS_KEY)
	public void removeFromCells(CSVCell cell);



	@Getter(value = ROW_INDEX_KEY, defaultValue = "-2")
	public int getRowIndex();


	@Setter(ROW_INDEX_KEY)
	public void setRowIndex(int index);




	public CSVCell getCellAt(int columnIndex);


	public CSVCell getCellByColumnName(String columnName);


	public String getCellValue(int columnIndex);


	public String getCellValue(String columnName);


	public void setCellValue(int columnIndex, String value);


	public void setCellValue(String columnName, String value);


	public int getCellCount();




	public static abstract class CSVRowImpl extends CSVObjectImpl implements CSVRow {

		private static final Logger logger = Logger.getLogger(CSVRow.class.getPackage().getName());


		@Override
		public CSVCell getCellAt(int columnIndex) {
			if (getCells() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get cell: cells list is null for row " + getRowIndex());
				}
				return null;
			}

			if (columnIndex < 0 || columnIndex >= getCells().size()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Column index " + columnIndex + " out of bounds for row " + getRowIndex()
							+ " (size: " + getCells().size() + ")");
				}
				return null;
			}

			return getCells().get(columnIndex);
		}


		@Override
		public int getCellCount() {
			return getCells() != null ? getCells().size() : 0;
		}


		@Override
		public CSVCell getCellByColumnName(String columnName) {
			if (columnName == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get cell: column name is null");
				}
				return null;
			}

			CSVDocument document = getCSVDocument();
			if (document == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get cell by column name: document is null");
				}
				return null;
			}

			CSVColumn column = document.getColumnByName(columnName);
			if (column == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Column not found: " + columnName);
				}
				return null;
			}

			return getCellAt(column.getColumnIndex());
		}


		@Override
		public String getCellValue(int columnIndex) {
			CSVCell cell = getCellAt(columnIndex);
			return cell != null ? cell.getValue() : null;
		}


		@Override
		public String getCellValue(String columnName) {
			CSVCell cell = getCellByColumnName(columnName);
			return cell != null ? cell.getValue() : null;
		}


		@Override
		public void setCellValue(int columnIndex, String value) {
			CSVCell cell = getCellAt(columnIndex);
			if (cell != null) {
				cell.setValue(value);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot set cell value: cell at index " + columnIndex + " does not exist");
				}
			}
		}


		@Override
		public void setCellValue(String columnName, String value) {
			if (columnName == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot set cell value: column name is null");
				}
				return;
			}

			CSVCell cell = getCellByColumnName(columnName);
			if (cell != null) {
				cell.setValue(value);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot set cell value: column '" + columnName + "' not found");
				}
			}
		}


		@Override
		public String toString() {
			return "CSVRow[index=" + getRowIndex() + ", cells=" + getCellCount() + "]";
		}
	}
}
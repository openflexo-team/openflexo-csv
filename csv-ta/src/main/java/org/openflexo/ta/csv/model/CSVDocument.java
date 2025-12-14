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

import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.Setter;


@ModelEntity
@ImplementationClass(value = CSVDocument.CSVDocumentImpl.class)
public interface CSVDocument extends CSVObject, ResourceData<CSVDocument> {


	@PropertyIdentifier(type = CSVColumn.class, cardinality = Cardinality.LIST)
	public static final String COLUMNS_KEY = "columns";

	@PropertyIdentifier(type = CSVRow.class, cardinality = Cardinality.LIST)
	public static final String ROWS_KEY = "rows";

	@PropertyIdentifier(type = String.class)
	public static final String DELIMITER_KEY = "delimiter";

	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_HEADER_KEY = "hasHeader";

	@PropertyIdentifier(type = CSVRow.class)
	public static final String HEADER_ROW_KEY = "headerRow";




	@Getter(value = DELIMITER_KEY, defaultValue = ",")
	public String getDelimiter();


	@Setter(DELIMITER_KEY)
	public void setDelimiter(String delimiter);



	@Getter(value = HAS_HEADER_KEY, defaultValue = "true")
	public boolean getHasHeader();


	@Setter(HAS_HEADER_KEY)
	public void setHasHeader(boolean hasHeader);




	@Getter(value = COLUMNS_KEY, cardinality = Cardinality.LIST,
			inverse = CSVColumn.CSV_DOCUMENT_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<CSVColumn> getColumns();


	@Adder(COLUMNS_KEY)
	public void addToColumns(CSVColumn column);


	@Remover(COLUMNS_KEY)
	public void removeFromColumns(CSVColumn column);



	@Getter(value = ROWS_KEY, cardinality = Cardinality.LIST,
			inverse = CSVRow.CSV_DOCUMENT_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<CSVRow> getRows();


	@Adder(ROWS_KEY)
	@PastingPoint
	public void addToRows(CSVRow row);


	@Remover(ROWS_KEY)
	public void removeFromRows(CSVRow row);


	@Getter(value = HEADER_ROW_KEY)
	public CSVRow getHeaderRow();


	@Setter(HEADER_ROW_KEY)
	public void setHeaderRow(CSVRow headerRow);



	@Override
	public CSVResource getResource();



	public CSVRow getRowAt(int index);


	public CSVColumn getColumnByName(String name);


	public CSVColumn getColumnAt(int index);


	public int getRowCount();


	public int getColumnCount();



	public static abstract class CSVDocumentImpl extends CSVObjectImpl implements CSVDocument {

		private static final Logger logger = Logger.getLogger(CSVDocument.class.getPackage().getName());

		private String delimiter = null;
		private Boolean hasHeader = null;


		@Override
		public String getDelimiter() {
			if (delimiter == null) {
				delimiter = (String) performSuperGetter(DELIMITER_KEY);
			}
			return delimiter;
		}

		@Override
		public void setDelimiter(String delimiter) {
			performSuperSetter(DELIMITER_KEY, delimiter);
			this.delimiter = delimiter;
		}

		@Override
		public boolean getHasHeader() {
			if (hasHeader == null) {
				hasHeader = (Boolean) performSuperGetter(HAS_HEADER_KEY);
			}
			return hasHeader != null ? hasHeader : true; // Default to true
		}

		@Override
		public void setHasHeader(boolean hasHeader) {
			performSuperSetter(HAS_HEADER_KEY, hasHeader);
			this.hasHeader = hasHeader;
		}

		@Override
		public void addToColumns(CSVColumn column) {
			if (column == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot add null column to document");
				}
				return;
			}
			performSuperAdder(COLUMNS_KEY, column);
		}

		@Override
		public void removeFromColumns(CSVColumn column) {
			if (column == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot remove null column from document");
				}
				return;
			}
			performSuperRemover(COLUMNS_KEY, column);
		}

		@Override
		public void addToRows(CSVRow row) {
			if (row == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot add null row to document");
				}
				return;
			}
			performSuperAdder(ROWS_KEY, row);// TODO CHANGE HERE FOR HEADER LOGIC
		}

		@Override
		public void removeFromRows(CSVRow row) {
			if (row == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot remove null row from document");
				}
				return;
			}
			performSuperRemover(ROWS_KEY, row);
		}



		@Override
		public CSVRow getRowAt(int index) {
			if (getRows() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get row: rows list is null");
				}
				return null;
			}

			if (index < 0 || index >= getRows().size()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Row index " + index + " out of bounds (size: " + getRows().size() + ")");
				}
				return null;
			}
			List<CSVRow> rows = getRows();
			return getRows().get(index);
		}

		@Override
		public CSVColumn getColumnByName(String name) {
			if (name == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get column: name is null");
				}
				return null;
			}

			if (getColumns() == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Cannot get column: columns list is null");
				}
				return null;
			}

			for (CSVColumn col : getColumns()) {
				if (col != null && name.equals(col.getName())) {
					return col;
				}
			}

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Column not found: " + name);
			}
			return null;
		}

		@Override
		public CSVColumn getColumnAt(int index) {
			if (getColumns() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot get column: columns list is null");
				}
				return null;
			}

			if (index < 0 || index >= getColumns().size()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Column index " + index + " out of bounds (size: " + getColumns().size() + ")");
				}
				return null;
			}

			return getColumns().get(index);
		}

		@Override
		public int getRowCount() {
			return getRows() != null ? getRows().size() : 0;
		}

		@Override
		public int getColumnCount() {
			return getColumns() != null ? getColumns().size() : 0;
		}

		@Override
		public CSVResource getResource() {
			return super.getResource();
		}


		@Override
		public String toString() {
			return "CSVDocument[rows=" + getRowCount() + ", columns=" + getColumnCount()
					+ ", hasHeader=" + getHasHeader() + ", delimiter='" + getDelimiter() + "']";
		}
	}
}
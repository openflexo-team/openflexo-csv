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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.pamela.annotations.*;


@ModelEntity
@ImplementationClass(CSVCell.CSVCellImpl.class)
@XMLElement
public interface CSVCell extends CSVObject {



    @PropertyIdentifier(type = CSVRow.class)
    public static final String CSV_ROW_KEY = "csvRow";

    @PropertyIdentifier(type = String.class)
    public static final String VALUE_KEY = "value";

    @PropertyIdentifier(type = Integer.class)
    public static final String COLUMN_INDEX_KEY = "columnIndex";



    @Getter(value = CSV_ROW_KEY, inverse = CSVRow.CELLS_KEY)
    public CSVRow getCSVRow();


    @Setter(CSV_ROW_KEY)
    public void setCSVRow(CSVRow row);



    @Getter(value = VALUE_KEY)
    public String getValue();


    @Setter(VALUE_KEY)
    public void setValue(String value);



    @Getter(value = COLUMN_INDEX_KEY, defaultValue = "-1")
    public int getColumnIndex();


    @Setter(COLUMN_INDEX_KEY)
    public void setColumnIndex(int index);




    public CSVColumn getColumn();



    public static abstract class CSVCellImpl extends CSVObjectImpl implements CSVCell {

        private static final Logger logger = Logger.getLogger(CSVCell.class.getPackage().getName());


        @Override
        public CSVColumn getColumn() {
            CSVRow row = getCSVRow();
            if (row == null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Cannot get column: row is null");
                }
                return null;
            }

            CSVDocument document = row.getCSVDocument();
            if (document == null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Cannot get column: document is null");
                }
                return null;
            }

            int columnIndex = getColumnIndex();
            if (columnIndex < 0) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Cannot get column: column index not set");
                }
                return null;
            }

            return document.getColumnAt(columnIndex);
        }


        @Override
        public String toString() {
            String value = getValue();
            if (value != null && value.length() > 20) {
                value = value.substring(0, 17) + "...";
            }
            return "CSVCell[column=" + getColumnIndex() + ", value=\"" + value + "\"]";
        }
    }
}
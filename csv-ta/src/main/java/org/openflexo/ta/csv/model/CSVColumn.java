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

import java.util.logging.Logger;

import org.openflexo.pamela.annotations.*;


@ModelEntity
@ImplementationClass(CSVColumn.CSVColumnImpl.class)
@XMLElement
public interface CSVColumn extends CSVObject {



    @PropertyIdentifier(type = CSVDocument.class)
    public static final String CSV_DOCUMENT_KEY = "csvDocument";

    @PropertyIdentifier(type = String.class)
    public static final String NAME_KEY = "name";

    @PropertyIdentifier(type = Integer.class)
    public static final String COLUMN_INDEX_KEY = "columnIndex";

    @PropertyIdentifier(type = String.class)
    public static final String DATA_TYPE_KEY = "dataType";



    @Getter(value = CSV_DOCUMENT_KEY, inverse = CSVDocument.COLUMNS_KEY)
    public CSVDocument getCSVDocument();


    @Setter(CSV_DOCUMENT_KEY)
    public void setCSVDocument(CSVDocument document);


    @Getter(value = NAME_KEY)
    public String getName();


    @Setter(NAME_KEY)
    public void setName(String name);



    @Getter(value = COLUMN_INDEX_KEY, defaultValue = "-1")
    public int getColumnIndex();


    @Setter(COLUMN_INDEX_KEY)
    public void setColumnIndex(int index);



    @Getter(value = DATA_TYPE_KEY, defaultValue = "String")
    public String getDataType();


    @Setter(DATA_TYPE_KEY)
    public void setDataType(String dataType);



    public static abstract class CSVColumnImpl extends CSVObjectImpl implements CSVColumn {

        @SuppressWarnings("unused")
        private static final Logger logger = Logger.getLogger(CSVColumn.class.getPackage().getName());


        @Override
        public String toString() {
            return "CSVColumn[index=" + getColumnIndex() + ", name=\"" + getName()
                    + "\", type=" + getDataType() + "]";
        }
    }
}
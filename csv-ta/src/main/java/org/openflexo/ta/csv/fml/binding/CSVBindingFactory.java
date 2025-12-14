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

package org.openflexo.ta.csv.fml.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;



public final class CSVBindingFactory extends TechnologyAdapterBindingFactory {

	static final Logger logger = Logger.getLogger(CSVBindingFactory.class.getPackage().getName());
	
	public CSVBindingFactory() {
		super();
	}


	
	@Override
	protected SimplePathElement<?> makeSimplePathElement(Object object, IBindingPathElement parent, Bindable bindable) {

		if (object instanceof CSVRow) {
			CSVRow row = (CSVRow) object;
			return new CSVDocumentCSVRowPathElement((BindingPathElement) parent,
					"row_" + row.getRowIndex(), CSVRow.class, bindable);
		}

		logger.warning("Unexpected object in makeSimplePathElement: " + object);
		return null;
	}

	
	@Override
	public boolean handleType(TechnologySpecificType<?> technologySpecificType) {
		if (technologySpecificType instanceof CSVDocument) {
			return true;
		}
		if (technologySpecificType instanceof CSVRow) {
			return true;
		}
		return true;
	}

	
	@Override
	public List<? extends SimplePathElement<?>> getAccessibleSimplePathElements(IBindingPathElement parent, Bindable bindable) {
		List<SimplePathElement<?>> returned = new ArrayList<>();

		// If parent is a CSVDocument, return all rows as accessible elements
		if (parent instanceof CSVDocument) {
			CSVDocument document = (CSVDocument) parent;
			for (CSVRow row : document.getRows()) {
				returned.add(getSimplePathElement(row, parent, bindable));
			}
		}

		return returned;
	}

	
	@Override
	public List<? extends FunctionPathElement<?>> getAccessibleFunctionPathElements(IBindingPathElement parent, Bindable bindable) {
		// TODO: Implement function path elements for CSV operations
		// Examples:
		// - document.getRowAt(int index)
		// - row.getValue(String columnName)
		// - document.findRows(String columnName, String value)
		return Collections.emptyList();
	}



}

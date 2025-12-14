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

import java.lang.reflect.Type;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElementImpl;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.ta.csv.model.CSVDocument;
//TODO THIS STILL DOESNT WORK ;

public class CSVDocumentCSVRowPathElement extends SimplePathElementImpl {

    
    public CSVDocumentCSVRowPathElement(BindingPathElement parent, String propertyName, Type type, Bindable bindable) {
        super(parent, propertyName, type, bindable);
    }

    
    @Override
    public String getLabel() {
        return getPropertyName();
    }

    
    @Override
    public String getTooltipText(Type resultingType) {
        return "row " + getPropertyName();
    }

    
    @Override
    public Object getBindingValue(Object target, BindingEvaluationContext context)
            throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {
        if (target instanceof CSVDocument) {
            CSVDocument document = (CSVDocument) target;
            // Extract row index from property name (e.g., "row_5" -> 5)
            String propertyName = getPropertyName();
            if (propertyName.startsWith("row_")) {
                try {
                    int rowIndex = Integer.parseInt(propertyName.substring(4));
                    return document.getRowAt(rowIndex);
                } catch (NumberFormatException e) {
                    // Invalid row index format
                    return null;
                }
            }
        }
        return null;
    }

    
    @Override
    public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
            throws TypeMismatchException, NullReferenceException {
        // CSV rows are not settable via bindings
    }

    
    @Override
    public boolean isResolved() {
        return true;
    }

    
    @Override
    public void resolve() {
        // Already resolved
    }
}
/**
 *
 * Copyright (c) 2018, Openflexo
 *
 * This file is part of CSVConnector, a component of the software infrastructure 
 * developed at Openflexo.
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

import org.openflexo.foundation.fml.rt.error.FMLNullReferenceError;
import org.openflexo.foundation.fml.rt.error.FMLEvaluation;
import org.openflexo.foundation.fml.rt.error.FMLExecutionException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.ta.csv.CSVModelSlot;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;


@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectCSVCell.AbstractSelectCSVCellImpl.class)
public interface AbstractSelectCSVCell<AT> extends AbstractFetchRequest<CSVModelSlot, CSVDocument, CSVCell, AT> {

    @PropertyIdentifier(type = DataBinding.class)
    public static final String CSV_DOCUMENT_KEY = "csvDocument";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String CSV_ROW_KEY = "csvRow";

    
    @Getter(value = CSV_DOCUMENT_KEY)
    @FMLAttribute(value = CSV_DOCUMENT_KEY)
    public DataBinding<CSVDocument> getCSVDocument();

    @Setter(CSV_DOCUMENT_KEY)
    public void setCSVDocument(DataBinding<CSVDocument> csvDocument);

    
    @Getter(value = CSV_ROW_KEY)
    @FMLAttribute(value = CSV_ROW_KEY)
    public DataBinding<CSVRow> getCSVRow();

    @Setter(CSV_ROW_KEY)
    public void setCSVRow(DataBinding<CSVRow> csvRow);

    public static abstract class AbstractSelectCSVCellImpl<AT>
            extends AbstractFetchRequestImpl<CSVModelSlot, CSVDocument, CSVCell, AT> implements AbstractSelectCSVCell<AT> {

        private static final Logger logger = Logger.getLogger(AbstractSelectCSVCell.class.getPackage().getName());

        private DataBinding<CSVDocument> csvDocument;
        private DataBinding<CSVRow> csvRow;

        public AbstractSelectCSVCellImpl() {
            super();
        }

        @Override
        public Type getFetchedType() {
            return CSVCell.class;
        }

        @Override
        public List<CSVCell> performExecute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

            List<CSVCell> selectedCells = new ArrayList<>();
            if (getCSVRow() != null && getCSVRow().isSet()) {
                // A row given, but null, is a null source (CORE-F-11)
                CSVRow specificRow = FMLEvaluation.evaluate(getCSVRow(), evaluationContext, this);
                if (specificRow == null) {
                    throw new FMLNullReferenceError(getStringRepresentation() + ": no CSV row to select from");
                }
                if (specificRow.getCells() != null) {
                    selectedCells.addAll(specificRow.getCells());
                }
            }
            else {
                CSVDocument document;
                if (getCSVDocument() != null && getCSVDocument().isSet()) {
                    document = FMLEvaluation.evaluate(getCSVDocument(), evaluationContext, this);
                }
                else {
                    document = getReceiver(evaluationContext);
                }
                if (document == null) {
                    throw new FMLNullReferenceError(getStringRepresentation() + ": no CSV document to select from");
                }
                if (document.getRows() != null) {
                    for (CSVRow row : document.getRows()) {
                        if (row.getCells() != null) {
                            selectedCells.addAll(row.getCells());
                        }
                    }
                }
            }
            return filterWithConditions(selectedCells, evaluationContext);
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
        public DataBinding<CSVRow> getCSVRow() {
            if (csvRow == null) {
                csvRow = new DataBinding<>(this, CSVRow.class, DataBinding.BindingDefinitionType.GET);
                csvRow.setBindingName("csvRow");
            }
            return csvRow;
        }

        @Override
        public void setCSVRow(DataBinding<CSVRow> csvRow) {
            if (csvRow != null) {
                csvRow.setOwner(this);
                csvRow.setDeclaredType(CSVRow.class);
                csvRow.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                csvRow.setBindingName("csvRow");
            }
            this.csvRow = csvRow;
        }
    }
}
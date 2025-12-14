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

import org.openflexo.foundation.PamelaResourceModelFactory;
import org.openflexo.foundation.action.FlexoUndoManager;
import org.openflexo.foundation.resource.PamelaResourceImpl.IgnoreLoadingEdits;
import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.ta.csv.rm.CSVResource;


public class CSVModelFactory extends PamelaModelFactory implements PamelaResourceModelFactory<CSVResource> {


	private static final Logger logger = Logger.getLogger(CSVModelFactory.class.getPackage().getName());


	private final CSVResource resource;


	private IgnoreLoadingEdits ignoreHandler = null;


	private FlexoUndoManager undoManager = null;


	public CSVModelFactory(CSVResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(CSVDocument.class));
		this.resource = resource;
		setEditingContext(editingContext);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created CSV model factory for resource: " + resource.getURI());
		}
	}


	@Override
	public CSVResource getResource() {
		return resource;
	}


	public CSVDocument makeCSVDocument() {
		CSVDocument document = newInstance(CSVDocument.class);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created new CSV document");
		}
		return document;
	}


	public CSVColumn makeCSVColumn(String name, int index) {
		CSVColumn column = newInstance(CSVColumn.class);
		column.setName(name);
		column.setColumnIndex(index);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created CSV column: name=" + name + ", index=" + index);
		}

		return column;
	}


	public CSVRow makeCSVRow(int index) {
		CSVRow row = newInstance(CSVRow.class);
		row.setRowIndex(index);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created CSV row: index=" + index);
		}

		return row;
	}


	public CSVCell makeCSVCell(String value, int columnIndex) {
		CSVCell cell = newInstance(CSVCell.class);
		cell.setValue(value);
		cell.setColumnIndex(columnIndex);

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Created CSV cell: column=" + columnIndex + ", value=" +
					(value != null && value.length() > 20 ? value.substring(0, 17) + "..." : value));
		}

		return cell;
	}



	@Override
	public synchronized void startDeserializing() {
		if (getResource() == null || getResource().getServiceManager() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot start deserializing: resource or service manager is null");
			}
			return;
		}

		EditingContext editingContext = getResource().getServiceManager().getEditingContext();

		if (editingContext != null && editingContext.getUndoManager() instanceof FlexoUndoManager) {
			undoManager = (FlexoUndoManager) editingContext.getUndoManager();
			undoManager.addToIgnoreHandlers(ignoreHandler = new IgnoreLoadingEdits(resource));

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Started loading CSV resource: " + resource.getURI());
			}
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("No undo manager available for resource loading");
			}
		}
	}


	@Override
	public synchronized void stopDeserializing() {
		if (ignoreHandler != null && undoManager != null) {
			undoManager.removeFromIgnoreHandlers(ignoreHandler);

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Finished loading CSV resource: " + resource.getURI());
			}

			ignoreHandler = null;
			undoManager = null;
		}
	}


	@Override
	public String toString() {
		return "CSVModelFactory[resource=" + (resource != null ? resource.getURI() : "null") + "]";
	}
}
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

package org.openflexo.ta.csv.rm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceFactory;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVModelFactory;

public class CSVResourceFactory
		extends TechnologySpecificPamelaResourceFactory<CSVResource, CSVDocument, CSVTechnologyAdapter, CSVModelFactory> {

	private static final Logger logger = Logger.getLogger(CSVResourceFactory.class.getPackage().getName());

	public static final String CSV_FILE_EXTENSION = ".csv";


	public CSVResourceFactory() throws ModelDefinitionException {
		super(CSVResource.class);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created CSVResourceFactory");
		}
	}


	@Override
	public CSVDocument makeEmptyResourceData(CSVResource resource) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Creating empty CSV document for resource: " + resource.getURI());
		}

		CSVDocument document = resource.getFactory().makeCSVDocument();

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created empty CSV document");
		}

		return document;
	}


	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (serializationArtefact == null || resourceCenter == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot validate null artifact or resource center");
			}
			return false;
		}

		String name = resourceCenter.retrieveName(serializationArtefact);
		if (name == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot retrieve name for artifact");
			}
			return false;
		}

		boolean hasValidExtension = name.toLowerCase().endsWith(CSV_FILE_EXTENSION);

		boolean isNotTemporary = !name.startsWith("~");

		boolean isValid = hasValidExtension && isNotTemporary;

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Artifact validation: " + name + " -> " + isValid);
		}

		return isValid;
	}





	@Override
	public <I> CSVResource registerResource(CSVResource resource, FlexoResourceCenter<I> resourceCenter) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Registering CSV resource: " + resource.getURI() + " in " + resourceCenter.getName());
		}

		super.registerResource(resource, resourceCenter);

		CSVTechnologyAdapter adapter = getTechnologyAdapter(resourceCenter.getServiceManager());
		if (adapter == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot get CSV technology adapter");
			}
			return resource;
		}

		CSVResourceRepository<?> repository = adapter.getCSVResourceRepository(resourceCenter);
		if (repository == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot get CSV resource repository for " + resourceCenter.getName());
			}
			return resource;
		}

		registerResourceInResourceRepository(resource, repository);

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Successfully registered CSV resource: " + resource.getURI());
		}

		return resource;
	}


	@Override
	public CSVModelFactory makeModelFactory(CSVResource resource,
											TechnologyContextManager<CSVTechnologyAdapter> technologyContextManager)
			throws ModelDefinitionException {

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Creating model factory for resource: " + resource.getURI());
		}

		if (technologyContextManager == null) {
			throw new IllegalArgumentException("Technology context manager cannot be null");
		}

		if (technologyContextManager.getServiceManager() == null) {
			throw new IllegalStateException("Service manager is null in technology context manager");
		}

		if (technologyContextManager.getServiceManager().getEditingContext() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Editing context is null - model factory may not work correctly");
			}
		}

		CSVModelFactory factory = new CSVModelFactory(
				resource,
				technologyContextManager.getServiceManager().getEditingContext()
		);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Created model factory: " + factory);
		}

		return factory;
	}


	@Override
	public String toString() {
		return "CSVResourceFactory[extension=" + CSV_FILE_EXTENSION + "]";
	}
}
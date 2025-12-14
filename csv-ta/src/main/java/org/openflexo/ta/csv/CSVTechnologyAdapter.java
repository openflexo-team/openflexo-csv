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

package org.openflexo.ta.csv;

import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactories;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.ta.csv.fml.binding.CSVBindingFactory;
import org.openflexo.ta.csv.rm.CSVResourceFactory;
import org.openflexo.ta.csv.rm.CSVResourceRepository;

import java.util.logging.Level;
import java.util.logging.Logger;



@DeclareModelSlots({ CSVModelSlot.class })
@DeclareResourceFactories({ CSVResourceFactory.class })
public class CSVTechnologyAdapter extends TechnologyAdapter<CSVTechnologyAdapter> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CSVTechnologyAdapter.class.getPackage().getName());


	private static final CSVBindingFactory BINDING_FACTORY = new CSVBindingFactory();

	@Override
	public String getName() {
		return "CSV technology adapter";
	}


	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/CSVTechnologyAdapter";
	}


	@Override
	public void ensureAllRepositoriesAreCreated(FlexoResourceCenter<?> rc) {
		super.ensureAllRepositoriesAreCreated(rc);
		getCSVResourceRepository(rc);
	}


	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (logger.isLoggable(Level.INFO)) {
			logger.fine("Checking if resource should be ignored: " + contents);
		}

		// Currently, we don't ignore any CSV files
		// This could be extended to filter based on file name patterns,
		// directories, or content inspection we'll figure it out later
		// TODO this method
		return false;
	}


	@Override
	public CSVBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}


	@Override
	public String getIdentifier() {
		return "CSV";
	}


	public CSVResourceFactory getCSVResourceFactory() {
		return getResourceFactory(CSVResourceFactory.class);
	}


	@SuppressWarnings("unchecked")
	public <I> CSVResourceRepository<I> getCSVResourceRepository(FlexoResourceCenter<I> resourceCenter) {
		CSVResourceRepository<I> returned = resourceCenter.retrieveRepository(CSVResourceRepository.class, this);
		if (returned == null) {
			returned = CSVResourceRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, CSVResourceRepository.class, this);

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Created new CSV resource repository for resource center: " + resourceCenter.getDefaultBaseURI());
			}
		}
		return returned;
	}




}

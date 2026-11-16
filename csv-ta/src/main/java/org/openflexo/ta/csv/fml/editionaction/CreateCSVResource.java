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

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.error.FMLCancellation;
import org.openflexo.foundation.fml.rt.error.FMLInternalError;
import org.openflexo.foundation.fml.rt.error.FMLNullReferenceError;
import org.openflexo.foundation.fml.rt.error.FMLTechnologyError;
import org.openflexo.foundation.fml.rt.error.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.ta.csv.CSVModelSlot;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVResourceFactory;


@ModelEntity
@ImplementationClass(CreateCSVResource.CreateCSVResourceImpl.class)
@FML("CreateCSVResource")
public interface CreateCSVResource extends AbstractCreateResource<CSVModelSlot, CSVDocument, CSVTechnologyAdapter> {

    public static abstract class CreateCSVResourceImpl
            extends AbstractCreateResourceImpl<CSVModelSlot, CSVDocument, CSVTechnologyAdapter> implements CreateCSVResource {

        private static final Logger logger = Logger.getLogger(CreateCSVResourceImpl.class.getPackage().getName());

        @Override
        public Type getAssignableType() {
            return CSVDocument.class;
        }

        @Override
        public CSVDocument execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

            String resourceName = getResourceName(evaluationContext);
            if (resourceName == null || resourceName.isEmpty()) {
                throw new FMLNullReferenceError("Cannot create CSV resource: resource name is null or empty");
            }
            String resourceURI = getResourceURI(evaluationContext);
            if (resourceURI == null || resourceURI.isEmpty()) {
                throw new FMLNullReferenceError("Cannot create CSV resource: resource URI is null or empty");
            }
            if (getResourceCenter(evaluationContext) == null) {
                throw new FMLNullReferenceError("Cannot create CSV resource: resource center is null");
            }
            CSVTechnologyAdapter csvTA = getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(CSVTechnologyAdapter.class);
            try {
                CSVResource newResource = createResource(csvTA, CSVResourceFactory.class, evaluationContext, ".csv", true);
                if (newResource == null) {
                    throw new FMLTechnologyError("Cannot create the CSV resource " + resourceName);
                }
                newResource.setModified(true);
                CSVDocument document = newResource.getResourceData();
                if (document == null) {
                    throw new FMLTechnologyError("The CSV resource " + resourceName + " was created but has no document");
                }
                return document;
            } catch (FMLExecutionException e) {
                // Already typed (a null name or resource center...): rethrown unchanged (CORE-F-11)
                throw e;
            } catch (ModelDefinitionException e) {
                throw new FMLInternalError(e);
            } catch (ResourceLoadingCancelledException e) {
                throw new FMLCancellation("Loading of the new CSV document was cancelled", e);
            } catch (FileNotFoundException | FlexoException e) {
                throw new FMLTechnologyError("Cannot create the CSV resource " + resourceName + ": " + e.getMessage(), e);
            }
        }
    }
}
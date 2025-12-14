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
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.ta.csv.CSVModelSlot;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.rm.CSVResource;
import org.openflexo.ta.csv.rm.CSVResourceFactory;


@ModelEntity
@ImplementationClass(CreateCSVResource.CreateCSVResourceImpl.class)
@XMLElement
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

            if (logger.isLoggable(Level.INFO)) {
                logger.info("Creating new CSV resource");
            }

            String resourceName = getResourceName(evaluationContext);
            String resourceURI = getResourceURI(evaluationContext);
            FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Resource name: " + resourceName);
                logger.fine("Resource URI: " + resourceURI);
                logger.fine("Resource center: " + rc);
            }

            if (resourceName == null || resourceName.isEmpty()) {
                String errorMsg = "Cannot create CSV resource: resource name is null or empty";
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(errorMsg);
                }
                throw new FMLExecutionException(errorMsg);
            }

            if (resourceURI == null || resourceURI.isEmpty()) {
                String errorMsg = "Cannot create CSV resource: resource URI is null or empty";
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(errorMsg);
                }
                throw new FMLExecutionException(errorMsg);
            }

            if (rc == null) {
                String errorMsg = "Cannot create CSV resource: resource center is null";
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(errorMsg);
                }
                throw new FMLExecutionException(errorMsg);
            }

            CSVTechnologyAdapter csvTA = getServiceManager().getTechnologyAdapterService()
                    .getTechnologyAdapter(CSVTechnologyAdapter.class);

            if (csvTA == null) {
                String errorMsg = "Cannot create CSV resource: CSV Technology Adapter not found";
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(errorMsg);
                }
                throw new FMLExecutionException(errorMsg);
            }

            CSVResource newResource;
            try {
                newResource = createResource(csvTA, CSVResourceFactory.class, evaluationContext, ".csv", true);

                if (newResource == null) {
                    String errorMsg = "Failed to create CSV resource";
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.severe(errorMsg);
                    }
                    throw new FMLExecutionException(errorMsg);
                }

                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Created new CSV resource: " + newResource);
                }

                newResource.setModified(true);

                CSVDocument document = newResource.getResourceData();

                if (document == null) {
                    String errorMsg = "CSV resource created but document is null";
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.severe(errorMsg);
                    }
                    throw new FMLExecutionException(errorMsg);
                }

                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Returning CSV document: " + document);
                }

                return document;

            } catch (ModelDefinitionException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Model definition error creating CSV resource: " + e.getMessage());
                }
                throw new FMLExecutionException(e);
            } catch (FileNotFoundException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("File not found creating CSV resource: " + e.getMessage());
                }
                throw new FMLExecutionException(e);
            } catch (ResourceLoadingCancelledException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Resource loading cancelled: " + e.getMessage());
                }
                throw new FMLExecutionException(e);
            } catch (SaveResourceException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Error saving CSV resource: " + e.getMessage());
                }
                throw new FMLExecutionException(e);
            } catch (FlexoException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Flexo exception creating CSV resource: " + e.getMessage());
                }
                throw new FMLExecutionException(e);
            } catch (Exception e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe("Unexpected error creating CSV resource: " + e.getMessage());
                }
                e.printStackTrace();
                throw new FMLExecutionException(e);
            }
        }
    }
}
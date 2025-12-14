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

package org.openflexo.ta.csv.fml;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FreeModelSlotInstance;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.csv.CSVModelSlot;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVObject;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.rm.CSVResource;


@ModelEntity
@ImplementationClass(CSVActorReference.CSVActorReferenceImpl.class)
@XMLElement
@FML("CSVActorReference")
public interface CSVActorReference<T extends CSVObject> extends ActorReference<T> {




	@PropertyIdentifier(type = String.class)
	public static final String OBJECT_URI_KEY = "objectURI";




	@Getter(value = OBJECT_URI_KEY)
	@XMLAttribute
	public String getObjectURI();


	@Setter(OBJECT_URI_KEY)
	public void setObjectURI(String objectURI);




	public abstract static class CSVActorReferenceImpl<T extends CSVObject> extends ActorReferenceImpl<T>
			implements CSVActorReference<T> {

		private static final Logger logger = FlexoLogger.getLogger(CSVActorReference.class.getPackage().toString());


		private T object;


		private String objectURI;


		public CSVActorReferenceImpl() {
			super();
		}



		public CSVDocument getCSVDocument() {
			if (getCSVResource() != null) {
				try {
					return getCSVResource().getResourceData();
				} catch (FileNotFoundException e) {
					logger.warning("CSV file not found: " + e.getMessage());
				} catch (ResourceLoadingCancelledException e) {
					logger.warning("CSV resource loading cancelled: " + e.getMessage());
				} catch (FlexoException e) {
					logger.warning("Error loading CSV resource: " + e.getMessage());
				}
			}
			return null;
		}


		public CSVResource getCSVResource() {
			FreeModelSlotInstance<?, ?, ?> msInstance = (FreeModelSlotInstance<?, ?, ?>) getModelSlotInstance();
			if (msInstance != null && msInstance.getResource() instanceof CSVResource) {
				return (CSVResource) msInstance.getResource();
			}
			return null;
		}




		@Override
		public T getModellingElement(boolean forceLoading) {
			// Return cached object if available
			if (object != null) {
				return object;
			}

			// Try to retrieve by URI if we have one
			if (objectURI != null) {
				logger.fine("Attempting to retrieve CSV object with URI: " + objectURI);

				CSVResource resource = getCSVResource();
				if (resource == null) {
					logger.warning("Cannot retrieve object: CSV resource is null");
					return null;
				}

				// Get the converter from the resource to deserialize the object
				// This follows the Excel pattern
				object = (T) resource.getConverter().fromSerializationIdentifier(objectURI);

				if (object != null) {
					logger.fine("Successfully retrieved CSV object: " + object);
				} else {
					logger.warning("Could not retrieve CSV object with URI: " + objectURI);
				}
			}

			return object;
		}


		@Override
		public void setModellingElement(T object) {
			this.object = object;
			if (object != null) {
				objectURI = object.getSerializationIdentifier();
			}
		}




		@Override
		public String getObjectURI() {
			if (object != null) {
				return object.getSerializationIdentifier();
			}
			return objectURI;
		}


		@Override
		public void setObjectURI(String objectURI) {
			if ((objectURI == null && this.objectURI != null)
					|| (objectURI != null && !objectURI.equals(this.objectURI))) {
				String oldValue = this.objectURI;
				this.objectURI = objectURI;
				// Clear cached object when URI changes
				this.object = null;
				getPropertyChangeSupport().firePropertyChange(OBJECT_URI_KEY, oldValue, objectURI);
			}
		}




		@Override
		public String toString() {
			return "CSVActorReference[uri=" + objectURI + ", object=" + (object != null ? object.getClass().getSimpleName() : "null") + "]";
		}
	}
}


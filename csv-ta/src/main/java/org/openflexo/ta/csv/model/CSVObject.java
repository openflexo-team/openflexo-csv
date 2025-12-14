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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.rm.CSVResource;

import java.io.FileNotFoundException;


@ModelEntity(isAbstract = true)
@ImplementationClass(CSVObject.CSVObjectImpl.class)
public interface CSVObject extends TechnologyObject<CSVTechnologyAdapter>, FlexoObject {



	@PropertyIdentifier(type = CSVResource.class)
	public static final String RESOURCE_KEY = "resource";


	@PropertyIdentifier(type = String.class)
	public static final String URI_KEY = "uri";




	@Getter(value = RESOURCE_KEY)
	public CSVResource getResource();


	@Setter(RESOURCE_KEY)
	public void setResource(CSVResource resource);



	@Getter(value = URI_KEY)
	public String getURI();


	@Setter(URI_KEY)
	public void setURI(String uri);


	public default String getSerializationIdentifier() {
		return getURI();
	}


	public CSVDocument getDocument() throws ResourceLoadingCancelledException, FlexoException, FileNotFoundException;




	default Class<CSVTechnologyAdapter> getTechnologyAdapterClass() {
		return CSVTechnologyAdapter.class;
	}




	public static abstract class CSVObjectImpl extends FlexoObjectImpl implements CSVObject {


		private CSVResource resource;


		protected String uri;



		@Override
		public CSVResource getResource() {
			int test = 0;
			return resource;
		}

		@Override
		public void setResource(CSVResource resource) {
			if (resource != this.resource) {
				CSVResource oldValue = this.resource;
				this.resource = resource;
				getPropertyChangeSupport().firePropertyChange(RESOURCE_KEY, oldValue, resource);
			}
		}

		public CSVResource getResourceData() {
			return getResource();
		}


		@Override
		public String getURI() {
			return uri;
		}

		@Override
		public void setURI(String uri) {
			if ((uri == null && this.uri != null)
					|| (uri != null && !uri.equals(this.uri))) {
				String oldValue = this.uri;
				this.uri = uri;
				getPropertyChangeSupport().firePropertyChange(URI_KEY, oldValue, uri);
			}
		}



		@Override
		public CSVDocument getDocument() throws ResourceLoadingCancelledException, FlexoException, FileNotFoundException {
			// If this is already a document, return self
			if (this instanceof CSVDocument) {
				return (CSVDocument) this;
			}

			// Otherwise, try to get document from resource
			if (resource != null && resource.getResourceData() != null) {
				return resource.getResourceData();
			}

			return null;
		}



		@Override
		public CSVTechnologyAdapter getTechnologyAdapter() {
			if (getResource() != null && getResource().getServiceManager() != null) {
				return getResource().getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(CSVTechnologyAdapter.class);
			}
			return null;
		}




		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + (uri != null ? uri : "no-uri") + "]";
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || !(obj instanceof CSVObject)) {
				return false;
			}

			CSVObject other = (CSVObject) obj;

			// Compare by URI if both have URIs
			if (this.getURI() != null && other.getURI() != null) {
				return this.getURI().equals(other.getURI());
			}

			// Otherwise use identity comparison
			return false;
		}


		@Override
		public int hashCode() {
			if (uri != null) {
				return uri.hashCode();
			}
			return super.hashCode();
		}
	}
}
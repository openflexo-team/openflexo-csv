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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.*;

import org.openflexo.ta.csv.fml.*;
import org.openflexo.ta.csv.fml.editionaction.*;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVObject;
import org.openflexo.ta.csv.rm.CSVResource;


@DeclareActorReferences({ CSVActorReference.class })
@DeclareFlexoRoles({
		CSVDocumentRole.class,
		CSVColumnRole.class,
		CSVRowRole.class,
		CSVCellRole.class
})
@DeclareEditionActions({
		CreateCSVResource.class,
		AddCSVRow.class,
		SelectCSVRow.class,
		AddCSVCell.class,
		SelectCSVCell.class,
})
@DeclareFetchRequests({
		SelectCSVRow.class,
		SelectCSVCell.class,
		SelectUniqueCSVRow.class,
		SelectUniqueCSVCell.class
})
@ModelEntity
@ImplementationClass(CSVModelSlot.CSVModelSlotImpl.class)
@XMLElement
@FML("CSVModelSlot")
public interface CSVModelSlot extends FreeModelSlot<CSVDocument, CSVResource> {


	@PropertyIdentifier(type = FlexoResource.class)
	public static final String TEMPLATE_RESOURCE_KEY = "templateResource";

	@PropertyIdentifier(type = String.class)
	public static final String TEMPLATE_CSV_URI_KEY = "templateCSVURI";


	@PropertyIdentifier(type = String.class)
	public static final String DELIMITER_KEY = "delimiter";

	@PropertyIdentifier(type = String.class)
	public static final String ENCODING_KEY = "encoding";


	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_HEADER_KEY = "hasHeader";


	@Getter(value = TEMPLATE_CSV_URI_KEY)
	@XMLAttribute
	public String getTemplateCSVURI();


	@Setter(TEMPLATE_CSV_URI_KEY)
	public void setTemplateCSVURI(String templateCSVURI);


	@Getter(TEMPLATE_RESOURCE_KEY)
	public CSVResource getTemplateResource();

	@Setter(TEMPLATE_RESOURCE_KEY)
	public void setTemplateResource(CSVResource templateResource);



	@Getter(value = DELIMITER_KEY, defaultValue = ",")
	@XMLAttribute
	public String getDelimiter();

	@Setter(DELIMITER_KEY)
	public void setDelimiter(String delimiter);


	@Getter(value = ENCODING_KEY, defaultValue = "UTF-8")
	@XMLAttribute
	public String getEncoding();


	@Setter(ENCODING_KEY)
	public void setEncoding(String encoding);

	@Getter(value = HAS_HEADER_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getHasHeader();

	@Setter(HAS_HEADER_KEY)
	public void setHasHeader(boolean hasHeader);




	public static abstract class CSVModelSlotImpl extends FreeModelSlotImpl<CSVDocument, CSVResource> implements CSVModelSlot {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(CSVModelSlot.class.getPackage().getName());

		/**
		 * Cache for CSV objects accessed by URI
		 */
		private final Map<String, CSVObject> uriCache = new HashMap<>();



		private CSVResource templateResource;

		protected String templateCSVURI;
 		protected String delimiter = ",";


		 protected String encoding = "UTF-8";


		protected boolean hasHeader = true;

		@Override
		public String getTemplateCSVURI() {
			if (getTemplateResource() != null) {
				return getTemplateResource().getURI();
			}
			return templateCSVURI;
		}

		@Override
		public void setTemplateCSVURI(String templateCSVURI) {
			if ((templateCSVURI == null && this.templateCSVURI != null)
					|| (templateCSVURI != null && !templateCSVURI.equals(this.templateCSVURI))) {
				String oldValue = this.templateCSVURI;
				this.templateCSVURI = templateCSVURI;
				getPropertyChangeSupport().firePropertyChange(TEMPLATE_CSV_URI_KEY, oldValue, templateCSVURI);
			}
		}

		@Override
		public CSVResource getTemplateResource() {
			if (templateResource == null && StringUtils.isNotEmpty(templateCSVURI)
					&& getServiceManager() != null && getServiceManager().getResourceManager() != null) {
				templateResource = (CSVResource) getServiceManager().getResourceManager()
						.getResource(templateCSVURI);
			}
			return templateResource;
		}

		@Override
		public void setTemplateResource(CSVResource templateResource) {
			if (templateResource != this.templateResource) {
				CSVResource oldValue = this.templateResource;
				this.templateResource = templateResource;
				getPropertyChangeSupport().firePropertyChange(TEMPLATE_RESOURCE_KEY, oldValue, templateResource);
			}
		}
		@Override
		public String getDelimiter() {
			return delimiter;
		}

		@Override
		public void setDelimiter(String delimiter) {
			if ((delimiter == null && this.delimiter != null)
					|| (delimiter != null && !delimiter.equals(this.delimiter))) {
				String oldValue = this.delimiter;
				this.delimiter = delimiter;
				getPropertyChangeSupport().firePropertyChange(DELIMITER_KEY, oldValue, delimiter);
			}
		}

		@Override
		public String getEncoding() {
			return encoding;
		}

		@Override
		public void setEncoding(String encoding) {
			if ((encoding == null && this.encoding != null)
					|| (encoding != null && !encoding.equals(this.encoding))) {
				String oldValue = this.encoding;
				this.encoding = encoding;
				getPropertyChangeSupport().firePropertyChange(ENCODING_KEY, oldValue, encoding);
			}
		}

		@Override
		public boolean getHasHeader() {
			return hasHeader;
		}

		@Override
		public void setHasHeader(boolean hasHeader) {
			if (this.hasHeader != hasHeader) {
				boolean oldValue = this.hasHeader;
				this.hasHeader = hasHeader;
				getPropertyChangeSupport().firePropertyChange(HAS_HEADER_KEY, oldValue, hasHeader);
			}
		}


		@Override
		public Class<CSVTechnologyAdapter> getTechnologyAdapterClass() {
			return CSVTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> patternRoleClass) {
			if (CSVDocumentRole.class.isAssignableFrom(patternRoleClass)) {
				return "document";
			}
			if (CSVColumnRole.class.isAssignableFrom(patternRoleClass)) {
				return "column";
			}
			if (CSVRowRole.class.isAssignableFrom(patternRoleClass)) {
				return "row";
			}
			if (CSVCellRole.class.isAssignableFrom(patternRoleClass)) {
				return "cell";
			}
			return null;
		}

		@Override
		public Type getType() {
			return CSVDocument.class;
		}

		@Override
		public CSVTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (CSVTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}



		public CSVObject getCSVObjectWithURI(String objectURI) {
			if (objectURI == null) {
				return null;
			}

			// Check cache first
			if (uriCache.containsKey(objectURI)) {
				return uriCache.get(objectURI);
			}

			// Parse URI and retrieve object
			CSVObject object = parseAndRetrieveObject(objectURI);
			if (object != null) {
				uriCache.put(objectURI, object);
			}
			return object;
		}

		public void clearURICache() {
			uriCache.clear();
		}



		protected CSVObject parseAndRetrieveObject(String uri) {
			if (uri == null || !uri.startsWith("csv://")) {
				logger.warning("Invalid CSV URI format: " + uri);
				return null;
			}

			// TODO: Implement full URI parsing logic
			// This is a placeholder that should be expanded based on your URI scheme
			logger.fine("Parsing CSV URI: " + uri);

			try {
				// Example parsing logic:
				// csv://document/row/5 -> get row at index 5
				String[] parts = uri.substring(6).split("/"); // Remove "csv://"

				if (parts.length >= 3 && "row".equals(parts[1])) {
					int rowIndex = Integer.parseInt(parts[2]);
					// Retrieve row from document
					// This would need access to the actual CSV document
					// return getAccessedResourceData().getRowAt(rowIndex);
				}

			} catch (Exception e) {
				logger.warning("Error parsing CSV URI: " + uri + " - " + e.getMessage());
			}

			return null;
		}














	}
}

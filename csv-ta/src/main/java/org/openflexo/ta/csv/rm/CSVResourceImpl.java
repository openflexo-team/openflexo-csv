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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FileWritingLock;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.StreamIODelegate;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVModelFactory;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.toolbox.FileUtils;


public abstract class CSVResourceImpl extends PamelaResourceImpl<CSVDocument, CSVModelFactory>
		implements CSVResource {

	private static final Logger logger = Logger.getLogger(CSVResourceImpl.class.getPackage().getName());


	private CSVConverter converter;

	CSVResourceImpl() {
		super();
	}


	@Override
	public CSVConverter getConverter() {
		if (converter == null) {
			converter = new CSVConverter(this);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created converter for resource: " + getURI());
			}
		}
		return converter;
	}


	@Override
	protected CSVDocument performLoad() throws IOException, Exception {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loading CSV resource: " + getURI());
		}

		converter = new CSVConverter(this);

		if (getFlexoIOStreamDelegate() == null) {
			throw new IOFlexoException("Cannot load CSV document with this IO/delegate: " + getIODelegate());
		}

		notifyResourceWillLoad();

		CSVDocument returned = null;
		try {
			returned = createOrLoadCSVDocument(getFlexoIOStreamDelegate());
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Exception loading CSV: " + e.getMessage());
			}
			throw e;
		}

		if (returned == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot retrieve resource data from serialization artifact: " + getIODelegate().toString());
			}
			return null;
		}

		notifyResourceLoaded();

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Successfully loaded CSV resource: " + getURI() + " (" + returned.getRowCount() + " rows)");
		}

		return returned;
	}

	@Override
	public void unloadResourceData(boolean deleteResourceData) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Unloading resource data: " + getURI());
		}

		super.unloadResourceData(deleteResourceData);

		if (converter != null) {
			converter.delete();
		}
		converter = null;
	}




	private void write(OutputStream out) throws SaveResourceException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Writing CSV to: " + getIODelegate().getSerializationArtefact());
		}

		CSVDocument document = getCSVDocument();
		if (document == null) {
			throw new SaveResourceException(getIODelegate(), new IllegalStateException("Document is null"));
		}

		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

			String delimiter = document.getDelimiter() != null ? document.getDelimiter() : ",";

			if (document.getHasHeader() && document.getHeaderRow() != null) {
				writeRow(writer, document.getHeaderRow(), delimiter);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Wrote header row");
				}
			}

			int rowCount = 0;
			for (CSVRow row : document.getRows()) {
				if (row != null) {
					writeRow(writer, row, delimiter);
					rowCount++;
				}
			}

			writer.flush();

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Successfully wrote " + rowCount + " rows to: " + getIODelegate().getSerializationArtefact());
			}

		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Error writing CSV: " + e.getMessage());
			}
			e.printStackTrace();
			throw new SaveResourceException(getIODelegate(), e);
		}
	}


	private void writeRow(PrintWriter writer, CSVRow row, String delimiter) {
		if (row == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempted to write null row");
			}
			return;
		}

		StringBuilder line = new StringBuilder();
		int cellCount = row.getCellCount();

		for (int i = 0; i < cellCount; i++) {
			if (i > 0) {
				line.append(delimiter);
			}

			CSVCell cell = row.getCellAt(i);
			String value = cell != null ? cell.getValue() : "";

 			if (value != null && needsQuoting(value, delimiter)) {
				value = "\"" + value.replace("\"", "\"\"") + "\"";
			}

			if (value != null) {
				line.append(value);
			}
		}

		writer.println(line.toString());
	}


	private boolean needsQuoting(String value, String delimiter) {
		return value.contains(delimiter) ||
				value.contains("\"") ||
				value.contains("\n") ||
				value.contains("\r");
	}

	@Override
	public Class<CSVDocument> getResourceDataClass() {
		return CSVDocument.class;
	}

	@Override
	protected void performSave(boolean clearIsModified) throws SaveResourceException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Saving CSV resource: " + this + " to " + getIODelegate().getSerializationArtefact());
		}

		if (getFlexoIOStreamDelegate() == null) {
			throw new SaveResourceException(getIODelegate(), new IllegalStateException("IO delegate is null"));
		}

		FileWritingLock lock = getFlexoIOStreamDelegate().willWriteOnDisk();

		try {
			if (getFlexoIOStreamDelegate() instanceof FileIODelegate) {
 				saveToFile((FileIODelegate) getFlexoIOStreamDelegate());
			} else {
 				write(getOutputStream());
			}

 			getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);

 			if (clearIsModified) {
				notifyResourceStatusChanged();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Resource status updated: " + getURI());
				}
			}

		} catch (SaveResourceException e) {
			getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
			throw e;
		} catch (Exception e) {
			getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Unexpected error saving CSV: " + e.getMessage());
			}
			throw new SaveResourceException(getIODelegate(), e);
		}
	}


	private void saveToFile(FileIODelegate fileDelegate) throws SaveResourceException {
		File temporaryFile = null;

		try {
			File fileToSave = fileDelegate.getFile();

			makeLocalCopy(fileToSave);

 			temporaryFile = fileDelegate.createTemporaryArtefact(".csv");
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Creating temporary file: " + temporaryFile.getAbsolutePath());
			}

 			try (FileOutputStream fos = new FileOutputStream(temporaryFile)) {
				write(fos);
			}

 			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Renaming " + temporaryFile + " to " + fileToSave);
			}
			FileUtils.rename(temporaryFile, fileToSave);

		} catch (IOException e) {
 			if (temporaryFile != null && temporaryFile.exists()) {
				if (!temporaryFile.delete()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Failed to delete temporary file: " + temporaryFile);
					}
				}
			}

			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Failed to save CSV resource: " + e.getMessage());
			}
			e.printStackTrace();
			throw new SaveResourceException(getIODelegate(), e);
		}
	}



	@Override
	public <I> CSVDocument createOrLoadCSVDocument(StreamIODelegate<I> ioDelegate) {
		if (ioDelegate == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IO delegate is null");
			}
			return null;
		}

		try {
			// If file doesn't exist, create new empty document
			if (!ioDelegate.exists()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Creating new empty CSV document (file doesn't exist)");
				}

				CSVDocument newDocument = getFactory().newInstance(CSVDocument.class);
				newDocument.setResource(this);
				return newDocument;
			}

			// Load existing file
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Loading existing CSV file: " + ioDelegate.getSerializationArtefactName());
			}
			CSVConverter.ParseConfig parseConfig = new CSVConverter.ParseConfig();
			CSVConverter converter = getConverter();

			try (InputStream inputStream = ioDelegate.getInputStream()) {
				CSVDocument loadedDocument = converter.loadCSVDocument(inputStream, parseConfig);

				if (loadedDocument != null) {
					loadedDocument.setResource(this);
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Converter returned null document");
					}
				}

				return loadedDocument;
			}// added the try here ot auto close the stream bcz it causes a problem when registering it keeps the file open

		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Error loading CSV: " + e.getMessage());
			}
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Unexpected error loading CSV: " + e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CSVDocument getCSVDocument() {
		try {
			return getResourceData();
		} catch (ResourceLoadingCancelledException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Resource loading cancelled: " + e.getMessage());
			}
			return null;
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("File not found: " + e.getMessage());
			}
			return null;
		} catch (FlexoException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Error getting CSV document: " + e.getMessage());
			}
			return null;
		}
	}
}
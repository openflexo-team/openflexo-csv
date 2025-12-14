package org.openflexo.ta.csv.gui;

import java.util.logging.Logger;
import javax.swing.ImageIcon;

import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVColumn;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;


public class CSVIconLibrary {

	private static final Logger logger = Logger.getLogger(CSVIconLibrary.class.getPackage().getName());



	
	public static final ImageIconResource CSV_TECHNOLOGY_BIG_ICON = loadIconOrDefault(
			"Icons/CSVBig.png",
			null
	);

	
	public static final ImageIconResource CSV_TECHNOLOGY_ICON = loadIconOrDefault(
			"Icons/CSVSmall.png",
			null
	);



	
	public static final ImageIconResource CSV_DOCUMENT_ICON = loadIconOrDefault(
			"Icons/CSVDocument.png",
			null
	);

	
	public static final ImageIconResource CSV_ROW_ICON = loadIconOrDefault(
			"Icons/CSVRow.png",
			null
	);

	
	public static final ImageIconResource CSV_CELL_ICON = loadIconOrDefault(
			"Icons/CSVCell.png",
			null
	);

	
	public static final ImageIconResource CSV_COLUMN_ICON = loadIconOrDefault(
			"Icons/CSVColumn.png",
			null
	);



	
	public static final ImageIcon ADD_CSV_ROW_ICON =
			IconFactory.getImageIcon(CSV_ROW_ICON, IconLibrary.DUPLICATE);

	
	public static final ImageIcon ADD_CSV_CELL_ICON =
			IconFactory.getImageIcon(CSV_CELL_ICON, IconLibrary.DUPLICATE);



	
	public static final ImageIconResource CSV_MARKER_ICON = loadIconOrDefault(
			"Icons/CSVMarker.png",
			null
	);

	
	public static final IconMarker CSV_MARKER = new IconMarker(CSV_MARKER_ICON, 8, 0);



	
	private static ImageIconResource loadIconOrDefault(String resourcePath, Resource defaultIcon) {
		try {
			return new ImageIconResource(ResourceLocator.locateResource(resourcePath));
		} catch (Exception e) {
			logger.fine("Icon not found at " + resourcePath + ", using default");
			return new ImageIconResource(defaultIcon);
		}
	}

	
	public static ImageIcon iconForObject(Class<? extends TechnologyObject<?>> objectClass) {
		if (CSVDocument.class.isAssignableFrom(objectClass)) {
			return CSV_DOCUMENT_ICON;
		}
		else if (CSVRow.class.isAssignableFrom(objectClass)) {
			return CSV_ROW_ICON;
		}
		else if (CSVCell.class.isAssignableFrom(objectClass)) {
			return CSV_CELL_ICON;
		}
		else if (CSVColumn.class.isAssignableFrom(objectClass)) {
			return CSV_COLUMN_ICON;
		}

		logger.warning("No icon defined for " + objectClass);
		return null;
	}
}
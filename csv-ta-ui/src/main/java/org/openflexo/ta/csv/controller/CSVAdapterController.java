package org.openflexo.ta.csv.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ta.csv.CSVTechnologyAdapter;
import org.openflexo.ta.csv.fml.CSVCellRole;
import org.openflexo.ta.csv.fml.CSVColumnRole;
import org.openflexo.ta.csv.fml.CSVDocumentRole;
import org.openflexo.ta.csv.fml.CSVRowRole;
import org.openflexo.ta.csv.fml.editionaction.AbstractSelectCSVCell;
import org.openflexo.ta.csv.fml.editionaction.AbstractSelectCSVRow;
import org.openflexo.ta.csv.fml.editionaction.AddCSVCell;
import org.openflexo.ta.csv.fml.editionaction.AddCSVRow;
import org.openflexo.ta.csv.gui.CSVIconLibrary;
import org.openflexo.ta.csv.model.CSVCell;
import org.openflexo.ta.csv.model.CSVColumn;
import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.ta.csv.view.CSVDocumentView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;


public class CSVAdapterController extends TechnologyAdapterController<CSVTechnologyAdapter> {

	private InspectorGroup csvInspectorGroup;

	@Override
	public Class<CSVTechnologyAdapter> getTechnologyAdapterClass() {
		return CSVTechnologyAdapter.class;
	}

	@Override
	protected void initializeInspectors(FlexoController controller) {
		csvInspectorGroup = controller.loadInspectorGroup("CSV",
				getTechnologyAdapter().getLocales(),
				getFMLTechnologyAdapterInspectorGroup());
	}

	@Override
	public InspectorGroup getTechnologyAdapterInspectorGroup() {
		return csvInspectorGroup;
	}

	@Override
	protected void initializeActions(ControllerActionInitializer actionInitializer) {
		// TODO: Add action initializers when needed
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		return CSVIconLibrary.CSV_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return CSVIconLibrary.CSV_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		return CSVIconLibrary.CSV_DOCUMENT_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return CSVIconLibrary.CSV_DOCUMENT_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject<?>> objectClass) {
		return CSVIconLibrary.iconForObject(objectClass);
	}

	@Override
	public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> flexoRoleClass) {
		if (CSVDocumentRole.class.isAssignableFrom(flexoRoleClass)) {
			return getIconForTechnologyObject(CSVDocument.class);
		}
		if (CSVRowRole.class.isAssignableFrom(flexoRoleClass)) {
			return getIconForTechnologyObject(CSVRow.class);
		}
		if (CSVCellRole.class.isAssignableFrom(flexoRoleClass)) {
			return getIconForTechnologyObject(CSVCell.class);
		}
		if (CSVColumnRole.class.isAssignableFrom(flexoRoleClass)) {
			return getIconForTechnologyObject(CSVColumn.class);
		}
		return null;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddCSVRow.class.isAssignableFrom(editionActionClass)) {
			return CSVIconLibrary.ADD_CSV_ROW_ICON;
		}
		else if (AddCSVCell.class.isAssignableFrom(editionActionClass)) {
			return CSVIconLibrary.ADD_CSV_CELL_ICON;
		}
		else if (AbstractSelectCSVRow.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(
					getIconForTechnologyObject(CSVRow.class),
					IconLibrary.IMPORT
			);
		}
		else if (AbstractSelectCSVCell.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(
					getIconForTechnologyObject(CSVCell.class),
					IconLibrary.IMPORT
			);
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public boolean isRepresentableInModuleView(TechnologyObject<CSVTechnologyAdapter> object) {
		return object instanceof CSVDocument;
	}

	@Override
	public FlexoObject getRepresentableMasterObject(TechnologyObject<CSVTechnologyAdapter> object) {
		if (object instanceof CSVDocument) {
			return object;
		}
		return null;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject<CSVTechnologyAdapter> object,
										  FlexoController controller) {
		if (object instanceof CSVDocument) {
			return ((CSVDocument) object).getResource().getName();
		}
		return object.toString();
	}

	@Override
	public ModuleView<?> createModuleViewForMasterObject(
			TechnologyObject<CSVTechnologyAdapter> object,
			FlexoController controller,
			FlexoPerspective perspective) {

		if (object instanceof CSVDocument) {
			return new CSVDocumentView((CSVDocument) object, controller, perspective);
		}

		return new EmptyPanel<>(controller, perspective, object);
	}
}
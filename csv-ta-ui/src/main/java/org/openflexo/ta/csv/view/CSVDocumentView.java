package org.openflexo.ta.csv.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.openflexo.ta.csv.model.CSVDocument;
import org.openflexo.ta.csv.model.CSVRow;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;


@SuppressWarnings("serial")
public class CSVDocumentView extends JPanel implements ModuleView<CSVDocument> {

    private final CSVDocument document;
    private final FlexoController controller;
    private final FlexoPerspective perspective;

    private final JTable table;
    private final CSVTableModel tableModel;

    
    public CSVDocumentView(CSVDocument document, FlexoController controller, FlexoPerspective perspective) {
        super();
        this.document = document;
        this.controller = controller;
        this.perspective = perspective;


        tableModel = new CSVTableModel(document);


        table = new JTable(tableModel);


        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);


        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(120);
        }


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    
    public FlexoController getFlexoController() {
        return controller;
    }

    
    @Override
    public FlexoPerspective getPerspective() {
        return perspective;
    }

    
    @Override
    public CSVDocument getRepresentedObject() {
        return document;
    }

    
    @Override
    public void deleteModuleView() {
        if (controller != null) {
            controller.removeModuleView(this);
        }
    }

    
    @Override
    public void willHide() {

    }

    
    @Override
    public void willShow() {

        tableModel.fireTableDataChanged();
    }

    
    @Override
    public void show(FlexoController controller, FlexoPerspective perspective) {

    }

    
    @Override
    public boolean isAutoscrolled() {
        return false;
    }

    
    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    
    public JTable getTable() {
        return table;
    }

    
    private static class CSVTableModel extends AbstractTableModel {

        private final CSVDocument document;
        private final boolean hasHeaders;

        
        public CSVTableModel(CSVDocument document) {
            this.document = document;


            this.hasHeaders = document.getRowCount() > 0;
        }

        
        @Override
        public int getRowCount() {
            int totalRows = document.getRowCount();
            return hasHeaders ? Math.max(0, totalRows - 1) : totalRows;
        }

        
        @Override
        public int getColumnCount() {
            if (document.getRowCount() == 0) return 0;


            int rowIndex = hasHeaders ? Math.min(1, document.getRowCount() - 1) : 0;
            if (rowIndex >= document.getRowCount()) return 0;

            return document.getRowAt(rowIndex).getCellCount();
        }

        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            int csvRowIndex = hasHeaders ? rowIndex + 1 : rowIndex;

            if (csvRowIndex >= document.getRowCount()) {
                return "";
            }

            CSVRow row = document.getRowAt(csvRowIndex);
            if (columnIndex >= row.getCellCount()) {
                return "";
            }

            return row.getCellAt(columnIndex).getValue();
        }

        
        @Override
        public String getColumnName(int column) {
            if (hasHeaders && document.getRowCount() > 0) {
                CSVRow headerRow = document.getRowAt(0);
                if (column < headerRow.getCellCount()) {
                    String headerValue = headerRow.getCellAt(column).getValue();
                    return headerValue.isEmpty() ? "Column " + (column + 1) : headerValue;
                }
            }
            return "Column " + (column + 1);
        }

        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {

            int csvRowIndex = hasHeaders ? rowIndex + 1 : rowIndex;

            if (csvRowIndex >= document.getRowCount()) {
                return;
            }

            CSVRow row = document.getRowAt(csvRowIndex);
            if (columnIndex >= row.getCellCount()) {
                return;
            }


            String newValue = value != null ? value.toString() : "";
            row.getCellAt(columnIndex).setValue(newValue);


            fireTableCellUpdated(rowIndex, columnIndex);


            if (document.getResource() != null) {
                document.getResource().setModified(true);
            }
        }

        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
}
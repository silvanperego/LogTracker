package org.sper.logtracker.servstat.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.plot.XYPlot;
import org.sper.logtracker.config.ConfigurationAware;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.proc.PublishingSemaphore;

public class ServiceControlPanel extends JPanel implements ConfigurationAware {

	private static final long serialVersionUID = 1L;
	private JTable controlTable;
	private ServiceControlTableModel controlTableModel;
	private JSpinner magFactSpinner;
	private ColorEditor cellEditor;
	private boolean keepConfig = false;
	private JButton btnApply;

	private static class ColorCellRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value != null) {
				JPanel panel = new JPanel();
				panel.setBackground((Color) value);
				return panel;
			}
			else 
				return null;
		}

	}
	
	public static class ConfData implements Serializable {

		private static final long serialVersionUID = 1L;

		Vector<Vector<Object>> data;

		Object magFact;
		
	}

	public ServiceControlPanel(ServiceStatsTabs serviceStatsTabs) {
		super();
		controlTable = new JTable();
		controlTableModel = new ServiceControlTableModel();
		controlTable.setModel(controlTableModel);
		controlTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		controlTable.getColumnModel().getColumn(2).setPreferredWidth(132);
		controlTable.getColumnModel().getColumn(3).setPreferredWidth(133);
		controlTable.getColumnModel().getColumn(5).setPreferredWidth(104);
		cellEditor = new ColorEditor();
		controlTable.getColumnModel().getColumn(10).setCellEditor(cellEditor);
		controlTable.getColumnModel().getColumn(10).setCellRenderer(new ColorCellRenderer());
		controlTable.setAutoCreateRowSorter(true);
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Service Call Statistics and Display Properties");
		add(lblNewLabel, BorderLayout.NORTH);
		
		JScrollPane scrollPanel = new JScrollPane(controlTable);
		add(scrollPanel);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(serviceStatsTabs.new ApplyControlAction());
		
		JPanel statMagPanel = new JPanel();
		FlowLayout fl_statMagPanel = (FlowLayout) statMagPanel.getLayout();
		fl_statMagPanel.setAlignment(FlowLayout.LEFT);
		buttonPanel.add(statMagPanel);
		
		JLabel lblStatisticsMagnificationFactor = new JLabel("Statistics Magnification Factor:");
		statMagPanel.add(lblStatisticsMagnificationFactor);
		
		magFactSpinner = new JSpinner();
		magFactSpinner.setModel(new SpinnerNumberModel(new Double(5), new Double(1), null, new Double(1)));
		magFactSpinner.setPreferredSize(new Dimension(50, 20));
		statMagPanel.add(magFactSpinner);
		btnApply.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnApply.setHorizontalAlignment(SwingConstants.TRAILING);
		btnApply.setEnabled(false);
		buttonPanel.add(btnApply);
		
	}
	
	public void clearTable() {
		if (keepConfig)
			keepConfig = false;
		else
			controlTableModel.setRowCount(0);
	}

	public JTable getTable() {
		return controlTable;
	}

	public PublishingSemaphore getPublishingSemaphore() {
		return cellEditor;
	}

	public void applyToSeriesCollection(NewPointExtractor newPointExtractor,
			Factor service, XYPlot xyPlot, CategoryCollection users) {
		controlTableModel.applyToSeriesCollection(newPointExtractor, service, xyPlot, users, (Double) magFactSpinner.getValue());
	}

	@Override
	public String getCompKey() {
		return "ServicePanel";
	}

	@Override
	public void applyConfig(Serializable cfg) {
		ConfData confData = (ConfData) cfg;
		controlTableModel.setRowCount(0);
		keepConfig = true;
		for (Vector<Object> row : confData.data) {
			for (int i = 1; i < 6; i++)
				row.set(i, null);
			controlTableModel.addRow(row);
		}
		magFactSpinner.setValue(confData.magFact);
	}

	/**
	 * Überprüft, ob in einer Row-Config-Element vorhanden sind.
	 * @param row die zu überprüfende Row
	 * @return true, falls Config vorhanden.
	 */
	private boolean rowHoldsConfig(Vector<Object> row) {
		boolean storeRow = false;
		for (int i = 6; i <= 9; i++)
			storeRow |= (Boolean) row.get(i);
		storeRow |= row.get(10) != null;
		return storeRow;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Serializable getConfig() {
		ConfData confData = new ConfData();
		confData.data = new Vector<Vector<Object>>();
		for (Vector<Object> row : (Vector<Vector<Object>>) controlTableModel.getDataVector()) {
			if (rowHoldsConfig(row))
				confData.data.add(row);
		}
		confData.magFact = magFactSpinner.getValue();
		return confData;
	}

	/**
	 * Löscht alle Einträge aus der Config-Tabelle, welche keine Checkbox enthalten.
	 * Setzt zudem alle Zähler auf null.
	 */
	public void cleanTable() {
		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> dataVector = (Vector<Vector<Object>>) controlTableModel.getDataVector();
		for (int i = dataVector.size(); --i >= 0; ) {
			Vector<Object> row = dataVector.get(i);
			if (rowHoldsConfig(row))
				for (int j = 1; j < 6; j++)
					row.set(j, null);
			else
				dataVector.remove(i);
		}
		controlTableModel.fireTableDataChanged();
	}

	public JComponent getApplyButton() {
		return btnApply;
	}

	@Override
	public boolean isDynamicModule() {
		return true;
	}
}

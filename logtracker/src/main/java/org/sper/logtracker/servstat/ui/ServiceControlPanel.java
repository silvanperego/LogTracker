package org.sper.logtracker.servstat.ui;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import org.jfree.chart.plot.XYPlot;
import org.sper.logtracker.config.compat.ConfigurationAware;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.proc.PublishingSemaphore;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serializable;
import java.util.Vector;

import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.CALLS_PER_MINUTE_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.COLOR_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.DO_MEAN_TIME_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.DO_MEDIAN_TIME_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.DO_PER_SECOND_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.DO_SCATTER_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.FIRST_STAT_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.FIRST_SWITCH_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.LAST_STAT_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.LAST_SWITCH_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.MEAN_RESPONSE_TIME_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.MEDIAN_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.NCOLS;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.SERVICE_NAME_COL;

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

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ServiceControlDataRow {
		@XmlValue
		String serviceName;
		@XmlAttribute
		boolean withScatter, withPerSecond, withMean, withMedian;
		@XmlAttribute
		Integer color;
	}

	public ServiceControlPanel(final ServiceStatsDockables serviceStatsTabs) {
		super();
		controlTable = new JTable();
		controlTableModel = new ServiceControlTableModel();
		controlTable.setModel(controlTableModel);
		controlTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		controlTable.getColumnModel().getColumn(CALLS_PER_MINUTE_COL).setPreferredWidth(132);
		controlTable.getColumnModel().getColumn(MEAN_RESPONSE_TIME_COL).setPreferredWidth(133);
		controlTable.getColumnModel().getColumn(MEDIAN_COL).setPreferredWidth(104);
		cellEditor = new ColorEditor();
		controlTable.getColumnModel().getColumn(COLOR_COL).setCellEditor(cellEditor);
		controlTable.getColumnModel().getColumn(COLOR_COL).setCellRenderer(new ColorCellRenderer());
		controlTable.setAutoCreateRowSorter(true);
		setLayout(new BorderLayout(0, 0));
		controlTable.addMouseListener(
		    serviceStatsTabs.new ShowServiceDetailAction(controlTable, "Service",
		        r -> (String) controlTableModel.getValueAt(r, SERVICE_NAME_COL),
		        f -> f.getService(),
		        dp -> dp.svcIdx, ServiceControlTableModel.LAST_STAT_COL));
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
		magFactSpinner.setModel(new SpinnerNumberModel(5.0, 1.0, null, 1.0));
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
			Factor service, XYPlot xyPlot, CategoryCollection users, Integer successRetCode) {
		controlTableModel.applyToSeriesCollection(newPointExtractor, service, xyPlot, users, (Double) magFactSpinner.getValue(), successRetCode);
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
			if (row.size() == NCOLS) {
				for (int i = FIRST_STAT_COL; i <= LAST_STAT_COL; i++)
					row.set(i, null);
				controlTableModel.addRow(row);
			}
		}
		magFactSpinner.setValue(confData.magFact);
	}

	public void applyXmlConfig(ServiceControlData scd) {
		controlTableModel.setRowCount(0);
		keepConfig = true;
		for (ServiceControlDataRow sdr : scd.getControlData()) {
			Object[] row = new Object[NCOLS];
			row[SERVICE_NAME_COL] = sdr.serviceName;
			row[DO_SCATTER_COL] = sdr.withScatter;
			row[DO_MEAN_TIME_COL] = sdr.withMean;
			row[DO_MEDIAN_TIME_COL] = sdr.withMedian;
			row[DO_PER_SECOND_COL] = sdr.withPerSecond;
			if (sdr.color != null)
				row[COLOR_COL] = new Color(sdr.color);
			controlTableModel.addRow(row);
		}
		magFactSpinner.setValue(scd.getMagFact());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ServiceControlData getConfig() {
		ServiceControlData scd = new ServiceControlData();
		for (Vector<Object> row : (Vector<Vector>) controlTableModel.getDataVector()) {
			if (rowHoldsConfig(row)) {
				ServiceControlDataRow sdr = new ServiceControlDataRow();
				sdr.serviceName = (String) row.get(SERVICE_NAME_COL);
				sdr.withScatter = (Boolean) row.get(DO_SCATTER_COL);
				sdr.withMean = (Boolean) row.get(DO_MEAN_TIME_COL);
				sdr.withMedian = (Boolean) row.get(DO_MEDIAN_TIME_COL);
				sdr.withPerSecond = (Boolean) row.get(DO_PER_SECOND_COL);
				final Color color = (Color) row.get(COLOR_COL);
				sdr.color = color != null ? color.getRGB() : null;
				scd.addControlData(sdr);
			}
		}
		scd.setMagFact((Double) magFactSpinner.getValue());
		return scd;
	}

	/**
	 * Überprüft, ob in einer Row-Config-Element vorhanden sind.
	 * @param row die zu überprüfende Row
	 * @return true, falls Config vorhanden.
	 */
	private boolean rowHoldsConfig(Vector<Object> row) {
		boolean storeRow = false;
		for (int i = FIRST_SWITCH_COL; i <= LAST_SWITCH_COL; i++)
			storeRow |= (Boolean) row.get(i);
		storeRow |= row.get(COLOR_COL) != null;
		return storeRow;
	}

	/**
	 * Löscht alle Einträge aus der Config-Tabelle, welche keine Checkbox enthalten.
	 * Setzt zudem alle Zähler auf null.
	 */
	public void cleanTable() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Vector<Vector> dataVector = (Vector<Vector>) controlTableModel.getDataVector();
		for (int i = dataVector.size(); --i >= 0; ) {
			@SuppressWarnings("unchecked")
			Vector<Object> row = dataVector.get(i);
			if (rowHoldsConfig(row))
				for (int j = FIRST_STAT_COL; j <= LAST_STAT_COL; j++)
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

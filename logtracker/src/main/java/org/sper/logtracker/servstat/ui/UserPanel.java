package org.sper.logtracker.servstat.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.sper.logtracker.config.compat.ConfigurationAware;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.proc.CategoryCollection;

public class UserPanel extends JPanel implements ConfigurationAware {

	private static final long serialVersionUID = 1L;
	private UserTableModel userTableModel;
	private JTable userTable;
	private boolean keepConfig = false;
	private JButton btnApply;

	public UserPanel(ServiceStatsTabs serviceStatsTabs) {
		userTableModel = new UserTableModel();
		userTable = new JTable();
		userTable.setModel(userTableModel);
		userTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		userTable.getColumnModel().getColumn(2).setPreferredWidth(132);
		userTable.getColumnModel().getColumn(3).setPreferredWidth(133);
		userTable.getColumnModel().getColumn(5).setPreferredWidth(104);
		userTable.setAutoCreateRowSorter(true);
		setLayout(new BorderLayout(0, 0));
		
		userTable.addMouseListener(
        serviceStatsTabs.new ShowServiceDetailAction(userTable, "User", 
            r -> (String) userTableModel.getValueAt(r, 0), 
            f -> f.getUser(), 
            dp -> dp.user, 4));
		JLabel lblNewLabel = new JLabel("Service Call Statistics per User");
		add(lblNewLabel, BorderLayout.NORTH);
		
		JScrollPane scrollPanel = new JScrollPane(userTable);
		add(scrollPanel);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(serviceStatsTabs.new ApplyControlAction());
		btnApply.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnApply.setHorizontalAlignment(SwingConstants.TRAILING);
		btnApply.setEnabled(false);
		buttonPanel.add(btnApply);		
	}

	public void clearTable() {
		if (keepConfig)
			keepConfig = false;
		else
			userTableModel.setRowCount(0);
	}

	public JTable getTable() {
		return userTable;
	}

	public CategoryCollection createUsersFilter(Factor user) {
		return userTableModel.createUsersFilter(user);
	}

	@Override
	public String getCompKey() {
		return "UserPanel";
	}

	@Override
	public void applyConfig(Serializable cfg) {
		@SuppressWarnings("unchecked")
		HashMap<String, Boolean> filterMap = (HashMap<String, Boolean>) cfg;
		userTableModel.setRowCount(0);
		keepConfig = true;
		for (Entry<String, Boolean> entry : filterMap.entrySet()) {
			Object[] obj = new Object[] {
					entry.getKey(), null, null, null, null, null, null, entry.getValue()
			};
			userTableModel.addRow(obj);
		}
	}

	public void applyConfig(ServiceControlData scd) {
		userTableModel.setRowCount(0);
		keepConfig = true;
		for (String entry : scd.getUserExclude()) {
			Object[] obj = new Object[] {
					entry, null, null, null, null, null, null, Boolean.FALSE
			};
			userTableModel.addRow(obj);
		}
	}
	
	public Serializable getConfig() {
		HashMap<String, Boolean> filterMap = new HashMap<String, Boolean>();
		for (int i = 0; i < userTableModel.getRowCount(); i++) {
			Boolean selected = (Boolean) userTableModel.getValueAt(i, 7);
			if (!selected)		// Beim User-Filter werden nur diejenigen User gespeichert, welche *nicht* angezeigt werden sollen.
				filterMap.put((String) userTableModel.getValueAt(i, 0), selected);
		}
		return filterMap;
	}

	public JComponent getApplyButon() {
		return btnApply;
	}

	@Override
	public boolean isDynamicModule() {
		return true;
	}

	public void addUserExludes(ServiceControlData config) {
		for (int i = 0; i < userTableModel.getRowCount(); i++) {
			Boolean selected = (Boolean) userTableModel.getValueAt(i, 7);
			if (!selected)		// Beim User-Filter werden nur diejenigen User gespeichert, welche *nicht* angezeigt werden sollen.
				config.addUserExclude((String) userTableModel.getValueAt(i, 0));
		}
	}
}

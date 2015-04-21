package org.sper.logtracker.erroranalysis.ui;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class LogLinePanel extends JPanel {
	
	public LogLinePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblMostRelevantErrors = new JLabel("Most relevant Errors");
		add(lblMostRelevantErrors, BorderLayout.NORTH);
		
		logLineTable = new JTable();
		logLineTableModel = new LogLineTableModel();
		logLineTable.setModel(logLineTableModel);
		logLineTable.getColumnModel().getColumn(0).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(1).setPreferredWidth(625);
		
		JScrollPane scrollPane = new JScrollPane(logLineTable);
		add(scrollPane, BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;
	private JTable logLineTable;
	private LogLineTableModel logLineTableModel;
	
	public LogLineTableModel getTableModel() {
		return logLineTableModel;
	}

}

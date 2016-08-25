package org.sper.logtracker.servstat.ui.detail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer;
import javax.swing.JScrollPane;

public class ServiceCallDetailViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable serviceDetailTable;
	private TableModel serviceDetailTableModel;

	public ServiceCallDetailViewer(String frameTitle, List<DataPoint> dataPointList, StatsDataPointFactorizer<DataPoint> factorizer) {
		super(frameTitle);		
		JLabel lblServiceCallDetails = new JLabel("Service Call Details of Service " + frameTitle);
		getContentPane().add(lblServiceCallDetails, BorderLayout.NORTH);
		
		serviceDetailTableModel = new ServiceCallDetailTableModel(dataPointList, factorizer);
		serviceDetailTable = new JTable(serviceDetailTableModel);
		serviceDetailTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		serviceDetailTable.getColumnModel().getColumn(1).setPreferredWidth(240);
		
		JScrollPane scrollPane = new JScrollPane(serviceDetailTable);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(e -> setVisible(false));
		buttonPanel.add(btnOk);
		setBounds(400, 100, 800, 859);
	}
}

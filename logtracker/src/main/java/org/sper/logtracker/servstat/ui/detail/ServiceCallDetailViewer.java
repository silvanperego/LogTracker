package org.sper.logtracker.servstat.ui.detail;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.ui.CorrelatedPopupMenuAction;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServiceCallDetailViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable serviceDetailTable;
	private ServiceCallDetailTableModel serviceDetailTableModel;

	public ServiceCallDetailViewer(String frameTitle, List<DataPoint> dataPointList, StatsDataPointFactorizer<DataPoint> factorizer, GlobalConfig globalConfig) {
		super(frameTitle);		
    setBounds(400, 100, 800, 859);
		JLabel lblServiceCallDetails = new JLabel("Service Call Details of " + frameTitle);
		getContentPane().add(lblServiceCallDetails, BorderLayout.NORTH);
		
		serviceDetailTableModel = new ServiceCallDetailTableModel(dataPointList, factorizer, globalConfig);
		serviceDetailTable = new JTable(serviceDetailTableModel);
		serviceDetailTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		serviceDetailTable.getColumnModel().getColumn(1).setPreferredWidth(240);
		
		serviceDetailTable.addMouseListener(new CorrelatedPopupMenuAction(serviceDetailTable, r -> serviceDetailTableModel.getDataPointAt(r), globalConfig));
		JScrollPane scrollPane = new JScrollPane(serviceDetailTable);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(e -> setVisible(false));
		buttonPanel.add(btnOk);
	}

}

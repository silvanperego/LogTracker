package org.sper.logtracker.correlation.ui;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.data.CorrelatedMessage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CorrelatedMessagesViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable corrTable;
	private JTextArea fullMessage;
	private CorrelatedMessagesTableModel tableModel;
	protected CorrelatedMessage selectedDataPoint;

	public CorrelatedMessagesViewer(JComponent owner, String correlationId, GlobalConfig globalConfig) {
		super("Correlated Messages");
		setBounds(100, 100, 1200, 759);
		getContentPane().setLayout(new BorderLayout());
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.setHorizontalAlignment(SwingConstants.RIGHT);
			okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JLabel lblErrorMessagesWithin = new JLabel("Error Messages within Correlation ID: " + correlationId);
			getContentPane().add(lblErrorMessagesWithin, BorderLayout.NORTH);
		}
		{
			tableModel = new CorrelatedMessagesTableModel(correlationId, globalConfig);
		}
		{
			corrTable = new JTable();
			corrTable.setModel(tableModel);
			corrTable.getColumnModel().getColumn(0).setPreferredWidth(100);
			corrTable.getColumnModel().getColumn(2).setPreferredWidth(80);
			corrTable.getColumnModel().getColumn(4).setPreferredWidth(669);
			corrTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectedDataPoint = tableModel.getDataPoint(corrTable.getSelectedRow());
					if (selectedDataPoint != null) {
						fullMessage.setText(selectedDataPoint.getDetail());
						fullMessage.setCaretPosition(0);
					} else
						fullMessage.setText(null);
					selectedDataPoint.getLogSource().triggerSelectionActions(selectedDataPoint);
				}
			});
			JScrollPane scrollPane = new JScrollPane(corrTable);
			scrollPane.setPreferredSize(new Dimension(700, 120));
			fullMessage = new JTextArea();
			fullMessage.setEditable(false);
			fullMessage.setLineWrap(true);
			
			JScrollPane messageScrollPane = new JScrollPane(fullMessage);
			messageScrollPane.setPreferredSize(new Dimension(700, 400));
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, messageScrollPane);
			getContentPane().add(splitPane, BorderLayout.CENTER);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setResizeWeight(0.4);
		}
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowDeactivated(WindowEvent e) {
				selectedDataPoint.getLogSource().triggerSelectionActions(null);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				if (selectedDataPoint != null)
					selectedDataPoint.getLogSource().triggerSelectionActions(selectedDataPoint);
			}
		});
	}
	
}

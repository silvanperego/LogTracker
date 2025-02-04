package org.sper.logtracker.erroranalysis.ui;

import org.jfree.chart.ChartPanel;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.ui.CorrelatedPopupMenuAction;
import org.sper.logtracker.erroranalysis.data.ErrorCategory;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.FileSnippet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CategoryViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable catMessageTable;
	private DefaultTableModel tableModel;
	private JTextArea fullMessage;
	private JCheckBox showDistributionBox;
	private ChartPanel chartPanel;
	private SimpleDateFormat sdf;

	/**
	 * Create the dialog.
	 * @param cat 
	 * @param table 
	 */
	CategoryViewer(JComponent owner, ErrorCategory cat, GlobalConfig globalConfig) {
		super("Message Category Detail View");
		setBounds(100, 100, 1200, 759);
		sdf = new SimpleDateFormat(globalConfig.getTimestampFormatStr());
		getContentPane().setLayout(new BorderLayout());
		{
			TemporalDistributionPlot temporalDistributionPlot = new TemporalDistributionPlot();
			chartPanel = temporalDistributionPlot.createPlotOnData(cat);
			chartPanel.setPreferredSize(new Dimension(1200, 250));
			chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
				
				showDistributionBox = new JCheckBox("Show Temporal Distribution Graph");
				showDistributionBox.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						chartPanel.setVisible(showDistributionBox.isSelected());
					}
				});
				showDistributionBox.setSelected(true);
				showDistributionBox.setHorizontalAlignment(SwingConstants.LEFT);
				buttonPane.add(showDistributionBox);
				
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			JLabel lblErrorMessagesWithin = new JLabel("Error Messages within this Category");
			getContentPane().add(lblErrorMessagesWithin, BorderLayout.NORTH);
		}
		{
			tableModel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Time", "User", "Source", "Message Content"
				}
			) {
				private static final long serialVersionUID = 1L;
				Class<?>[] columnTypes = new Class[] {
					String.class, String.class, String.class, Object.class
				};
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			synchronized(cat) {
				for (int i = cat.getNumMessages(); --i >= 0; ) {
					RawErrorDataPoint dp = cat.getErrorsAsList().get(i);
					tableModel.addRow(new Object[] {
							sdf.format(new Date(dp.occTime)),
							dp.user,
							dp.logSource,
							dp
					});
				}
			}
		}
		
		{
			catMessageTable = new JTable();
			catMessageTable.setModel(tableModel);
			catMessageTable.getColumnModel().getColumn(0).setPreferredWidth(100);
			catMessageTable.getColumnModel().getColumn(2).setPreferredWidth(80);
			catMessageTable.getColumnModel().getColumn(3).setPreferredWidth(669);
			catMessageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					int selectedRow = catMessageTable.getSelectedRow();
					if (selectedRow >= 0) {
						FileSnippet snippet = ((RawErrorDataPoint) tableModel.getValueAt(selectedRow, 3)).fileSnippet;
						fullMessage.setText(snippet.getContents());
						fullMessage.setCaretPosition(0);
					} else
						fullMessage.setText(null);
				}
			});
			catMessageTable.addMouseListener(new CorrelatedPopupMenuAction(catMessageTable, r -> tableModel.getValueAt(r, 3), globalConfig));
			JScrollPane scrollPane = new JScrollPane(catMessageTable);
			scrollPane.setPreferredSize(new Dimension(700, 120));
			fullMessage = new JTextArea();
			fullMessage.setEditable(false);
			fullMessage.setLineWrap(true);
			
			FlowLayout flowLayout = (FlowLayout) chartPanel.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			JScrollPane messageScrollPane = new JScrollPane(fullMessage);
			messageScrollPane.setPreferredSize(new Dimension(700, 400));
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, messageScrollPane);
			getContentPane().add(chartPanel, BorderLayout.NORTH);
			getContentPane().add(splitPane, BorderLayout.CENTER);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setResizeWeight(0.4);
		}
	}

	DefaultTableModel getTableModel() {
		return tableModel;
	}
	
}

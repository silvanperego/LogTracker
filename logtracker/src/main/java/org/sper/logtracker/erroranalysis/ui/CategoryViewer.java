package org.sper.logtracker.erroranalysis.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;
import org.sper.logtracker.correlation.ui.CorrelatedMessagesViewer;
import org.sper.logtracker.erroranalysis.data.ErrorCategory;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.FileSnippet;

public class CategoryViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable catMessageTable;
	private DefaultTableModel tableModel;
	private JTextArea fullMessage;
	private JCheckBox showDistributionBox;
	private ChartPanel chartPanel;

	/**
	 * Create the dialog.
	 * @param cat 
	 * @param table 
	 */
	CategoryViewer(JComponent owner, ErrorCategory cat) {
		super("Message Category Detail View");
		setBounds(100, 100, 1200, 759);
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
					Object.class, String.class, String.class, Object.class
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
							new Date(dp.occTime),
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
			catMessageTable.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						int r = catMessageTable.rowAtPoint(e.getPoint());
						if (r >= 0 && r < catMessageTable.getRowCount()) {
							JPopupMenu popup = new JPopupMenu();
							final JMenuItem menuItem = new JMenuItem("Search for correlated Messages");
							menuItem.addActionListener(aev -> showCorrelationTableForId(r));
							popup.add(menuItem);
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
			});
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
	
	private void showCorrelationTableForId(int rowNum) {
		RawErrorDataPoint dataPoint = (RawErrorDataPoint) tableModel.getValueAt(rowNum, 3);
		new CorrelatedMessagesViewer(catMessageTable, dataPoint.correlationId).setVisible(true);
	}
	
}

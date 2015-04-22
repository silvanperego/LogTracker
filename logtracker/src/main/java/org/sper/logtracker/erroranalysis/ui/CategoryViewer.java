package org.sper.logtracker.erroranalysis.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.FileSnippet;

public class CategoryViewer extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable catMessageTable;
	private DefaultTableModel tableModel;
	private JTextArea fullMessage;

	/**
	 * Create the dialog.
	 * @param table 
	 */
	CategoryViewer(JComponent owner) {
		super(JOptionPane.getFrameForComponent(owner), "Message Category Detail View");
		setBounds(100, 100, 1200, 759);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
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
					"Time", "User", "Message Content"
				}
			) {
				Class[] columnTypes = new Class[] {
					Object.class, String.class, Object.class
				};
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}
		{
			catMessageTable = new JTable();
			catMessageTable.setModel(tableModel);
			catMessageTable.getColumnModel().getColumn(0).setPreferredWidth(100);
			catMessageTable.getColumnModel().getColumn(2).setPreferredWidth(669);
			catMessageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					int selectedRow = catMessageTable.getSelectedRow();
					if (selectedRow >= 0) {
						FileSnippet snippet = ((RawErrorDataPoint) tableModel.getValueAt(selectedRow, 2)).fileSnippet;
						fullMessage.setText(snippet.getContents());
						fullMessage.setCaretPosition(0);
					} else
						fullMessage.setText(null);
				}
			});
			JScrollPane scrollPane = new JScrollPane(catMessageTable);
			fullMessage = new JTextArea();
			fullMessage.setEditable(false);
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, new JScrollPane(fullMessage));
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			getContentPane().add(splitPane, BorderLayout.CENTER);
		}
	}

	DefaultTableModel getTableModel() {
		return tableModel;
	}
	
}

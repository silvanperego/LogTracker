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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CategoryViewer extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable catMessageTable;
	private DefaultTableModel tableModel;

	/**
	 * Create the dialog.
	 * @param table 
	 */
	CategoryViewer(JComponent owner) {
		super(JOptionPane.getFrameForComponent(owner), "Message Category Detail View");
		setBounds(100, 100, 685, 536);
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
			catMessageTable = new JTable();
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
			};
			catMessageTable.setModel(tableModel);
			catMessageTable.getColumnModel().getColumn(0).setPreferredWidth(100);
			catMessageTable.getColumnModel().getColumn(2).setPreferredWidth(669);
			JScrollPane scrollPane = new JScrollPane(catMessageTable);
			getContentPane().add(scrollPane, BorderLayout.CENTER);
		}
	}

	DefaultTableModel getTableModel() {
		return tableModel;
	}
	
}

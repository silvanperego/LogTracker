package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.sper.logtracker.data.Console.MessageListener;

public class LogFilePanel extends JPanel implements MessageListener {

	private static final long serialVersionUID = 1L;
	private JTextArea warnings;
	private JButton btnClearLog;

	public LogFilePanel() {
		setLayout(new BorderLayout(0, 0));
		warnings = new JTextArea();
		warnings.setBackground(UIManager.getColor("EditorPane.disabledBackground"));
		warnings.setEditable(false);
		warnings.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(warnings);
		add(scrollPane);

		JPanel logButtonPanel = new JPanel();
		add(logButtonPanel, BorderLayout.SOUTH);
		logButtonPanel.setLayout(new BorderLayout(0, 0));

		btnClearLog = new JButton("Clear Log");
		btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				warnings.setText("");
				btnClearLog.setVisible(false);
			}
		});
		btnClearLog.setVisible(false);
		btnClearLog.setHorizontalAlignment(SwingConstants.RIGHT);
		logButtonPanel.add(btnClearLog, BorderLayout.EAST);
		btnClearLog.setIcon(new ImageIcon(LogTracker.class.getResource("/delFile.png")));
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.ui.MessageListener#addMessage(java.lang.String)
	 */
	@Override
	public void addMessage(String text) {
		warnings.setText(warnings.getText() + text);
		btnClearLog.setVisible(true);
	}

}

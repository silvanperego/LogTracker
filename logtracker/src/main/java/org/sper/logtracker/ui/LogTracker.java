package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UnsupportedLookAndFeelException;

import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.servstat.ServiceStatsTabs;

public class LogTracker {

	private static String cfgFile;
	private static List<String> fnameList = new ArrayList<String>();
	private JFrame frame;
	private JTabbedPane tabbedPane;
	private Configuration configuration = new Configuration();
	private FileControlPanel fileControlPanel;
	private ServiceStatsTabs serviceStatsTabs;

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		try {
			for (String arg : args) {
				if (arg.endsWith(".ltc")) {
					if (cfgFile != null) {
						System.err.println("Only one Config-File can be specified!");
						showUsage();
					}
					cfgFile = arg;
				} else {
					fnameList.add(arg);
				}
			}
			if (cfgFile != null && !fnameList.isEmpty()) {
				System.err.println("You must either specify a Config-File or a Log-File.");
				showUsage();
			}
		} catch (Exception e) {
			showUsage();
		}
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					LogTracker window = new LogTracker();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void showUsage() {
		System.err.println("Usage: LogTracker <cfg-file-name>|{<log-file-name>} [-obsval=<obsval_in_minutes>]");
		System.exit(1);
	}

	/**
	 * Create the application.
	 * 
	 * @param fname
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public LogTracker() throws IOException, InterruptedException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param fname
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void initialize() throws IOException,
			InterruptedException {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(LogTracker.class.getResource("/LogTrackerLogo.png")));
		frame.setBounds(100, 100, 986, 804);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Log-Tracker");

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		fileControlPanel = new FileControlPanel(this, fnameList);
		tabbedPane.addTab("Config", null, fileControlPanel, null);
		configuration.registerModule(fileControlPanel);
		serviceStatsTabs = new ServiceStatsTabs(tabbedPane, this, configuration);
		if (cfgFile != null) {
			try {
				configuration.loadConfiguration(new File(cfgFile));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "Error when loading Config-File", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	Configuration getConfiguration() {
		return configuration;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public static void setCfgFile(String cfgFile) {
		LogTracker.cfgFile = cfgFile;
	}

	public void setTabIdx(int idx) {
		tabbedPane.setSelectedIndex(idx);
	}

	public FileControlPanel getFileControlPanel() {
		return fileControlPanel;
	}

	public void setupDataPipeLines(List<String> fname, LogParser selectedItem) {
		serviceStatsTabs.setupDataPipeLines(fname, selectedItem);
	}

}

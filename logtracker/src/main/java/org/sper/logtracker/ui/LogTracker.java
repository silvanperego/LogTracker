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
import javax.swing.UnsupportedLookAndFeelException;

import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.logreader.LogSource;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;

public class LogTracker {

	private static String cfgFile;
	private static List<LogSource> fnameList = new ArrayList<LogSource>();
	private JFrame frame;
	private Configuration configuration = new Configuration();
	private FileControlPanel fileControlPanel;
	private ToolBar toolBar;
	private CControl control;

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
					fnameList.add(new LogSource(arg));
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
		frame.setLayout(new BorderLayout(0, 2));
		toolBar = new ToolBar(this);
		frame.add(toolBar, BorderLayout.NORTH);

		control = new CControl(frame);
		frame.add(control.getContentArea(), BorderLayout.CENTER);
		final LogFilePanel logFilePanel = new LogFilePanel();
		fileControlPanel = new FileControlPanel(this, fnameList, logFilePanel, toolBar);
		final DefaultSingleCDockable fileSelectionDockable = new DefaultSingleCDockable("File Selection", "File Selection", fileControlPanel);
		control.addDockable(fileSelectionDockable);
		DefaultSingleCDockable logFileDockable = new DefaultSingleCDockable("OwnLogs", "Log File Reading Errors", logFilePanel);
		control.addDockable(logFileDockable);
		fileSelectionDockable.setLocation(CLocation.base().normal().stack(0));
		logFileDockable.setLocation(CLocation.base().normal().stack(1));
		logFileDockable.setVisible(true);
		fileSelectionDockable.setVisible(true);
		configuration.registerModule(fileControlPanel);
		if (cfgFile != null) {
			try {
				configuration.loadConfiguration(new File(cfgFile));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "Error when loading Config-File", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public static void setCfgFile(String cfgFile) {
		LogTracker.cfgFile = cfgFile;
	}

	public void setTabIdx(int idx) {
//		tabbedPane.setSelectedIndex(idx);
	}

	public FileControlPanel getFileControlPanel() {
		return fileControlPanel;
	}

	void setTitle(String title) {
		frame.setTitle(title);
	}

	public CControl getControl() {
		return control;
	}

}

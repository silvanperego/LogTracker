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
import org.sper.logtracker.parserconf.ConfiguredLogParser;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;

public class LogTracker {

	private static String cfgFile;
	private static List<LogSource> fnameList = new ArrayList<LogSource>();
	private JFrame frame;
	private ToolBar toolBar;
	private CControl control;
	private LogFilePanel logFilePanel;
	private List<ConfiguredLogParser<?>> parserConfigCatalog = new ParserConfigCatalog();

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
		logFilePanel = new LogFilePanel();
		DefaultSingleCDockable logFileDockable = new DefaultSingleCDockable("OwnLogs", "Log File Reading Errors", logFilePanel);
		control.addDockable(logFileDockable);
		logFileDockable.setLocation(CLocation.base().normal().stack());
		logFileDockable.setVisible(true);
		Configuration configuration = new Configuration();
		FileControlPanel fileControlPanel = addNewFileControl(configuration, CLocation.base().normal().stack(), fnameList);
		configuration.registerModule(fileControlPanel);
		if (cfgFile != null) {
			try {
				configuration.loadConfiguration(new File(cfgFile));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "Error when loading Config-File", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	FileControlPanel addNewFileControl(Configuration config, CLocation location, List<LogSource> fnameList) {
		FileControlPanel fileControlPanel = new FileControlPanel(this, fnameList, logFilePanel, toolBar, config, parserConfigCatalog );
		final DefaultMultipleCDockable fileSelectionDockable = new DefaultMultipleCDockable(null, "File Selection", fileControlPanel);
		control.addDockable(fileSelectionDockable);
		fileSelectionDockable.setLocation(location);
		fileSelectionDockable.setCloseable(true);
		fileSelectionDockable.setVisible(true);
		return fileControlPanel;
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

	void setTitle(String title) {
		frame.setTitle(title);
	}

	public CControl getControl() {
		return control;
	}

}

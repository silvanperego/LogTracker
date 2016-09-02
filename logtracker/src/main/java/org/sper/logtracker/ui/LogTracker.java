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

import org.sper.logtracker.config.FileControl;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.LogTrackerConfig;
import org.sper.logtracker.config.XMLConfigSupport;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.data.Console;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;

public class LogTracker extends JFrame {

	private static final long serialVersionUID = 1L;
	private static String cfgFile;
	private static List<LogSource> fnameList = new ArrayList<LogSource>();
	private ToolBar toolBar;
	private CControl control;
	private LogFilePanel logFilePanel;
	private ParserConfigCatalog parserConfigCatalog = new ParserConfigCatalog();
	private List<FileControlPanel> fileControlPanelList = new ArrayList<>();
	private GlobalConfig globalConfig = new GlobalConfig();

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
					window.setVisible(true);
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
		setIconImage(Toolkit.getDefaultToolkit().getImage(LogTracker.class.getResource("/LogTrackerLogo.png")));
		setBounds(100, 100, 986, 804);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(0, 2));
		toolBar = new ToolBar(this, parserConfigCatalog);
		add(toolBar, BorderLayout.NORTH);

		control = new CControl(this);
		add(control.getContentArea(), BorderLayout.CENTER);
		logFilePanel = new LogFilePanel();
		Console.setListener(logFilePanel);
		if (cfgFile != null)
			applyConfigurationFile(new File(cfgFile));
		else
			addNewFileControl();
		DefaultSingleCDockable logFileDockable = new DefaultSingleCDockable("OwnLogs", "Log File Reading Errors", logFilePanel);
		control.addDockable(logFileDockable);
		logFileDockable.setLocation(CLocation.base().normal().stack());
		logFileDockable.setVisible(true);
	}

	FileControlPanel addNewFileControl() {
		final FileControlPanel fileControlPanel = new FileControlPanel(this, parserConfigCatalog);
		final DefaultMultipleCDockable fileSelectionDockable = new DefaultMultipleCDockable(null, "File Selection", fileControlPanel);
		control.addDockable(fileSelectionDockable);
		fileSelectionDockable.setLocation(CLocation.base().normalSouth(0.3));
		fileSelectionDockable.setCloseable(true);
		fileSelectionDockable.setVisible(true);
		this.fileControlPanelList.add(fileControlPanel);
		fileSelectionDockable.addVetoClosingListener(new CVetoClosingListener() {
			
			@Override
			public void closing(CVetoClosingEvent event) {
			}
			
			@Override
			public void closed(CVetoClosingEvent event) {
				fileControlPanel.cascadeDelete();
				LogTracker.this.fileControlPanelList.remove(fileControlPanel);
			}
		});
		return fileControlPanel;
	}

	public void applyConfigurationFile(File selectedFile) {
		try {
			LogTrackerConfig xmlConfig = new XMLConfigSupport().loadConfiguration(selectedFile);
			if (xmlConfig == null) {
				// Das war wohl kein XML-File. Versuche alte Konfiguration mit Java Deserialisierung zu laden.
				Configuration configuration = new Configuration();
				FileControlPanel fileControlPanel = addNewFileControl();
				fileControlPanel.setConfig(configuration);
				configuration.registerModule(fileControlPanel);
				configuration.loadConfiguration(selectedFile);
			} else
				applyConfig(xmlConfig);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error when loading Config-File: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void setCfgFile(String cfgFile) {
		LogTracker.cfgFile = cfgFile;
	}

	public CControl getControl() {
		return control;
	}

	/**
	 * Erstelle ein Konfigurationsobjekt, welches die aktuelle Konfiguration der Applikation beschreibt, inklusive
	 * der Konfigurierten Fenster.
	 * @return das Konfigurationsobjekt.
	 */
	public LogTrackerConfig createConfigurationTree() {
		LogTrackerConfig config = new LogTrackerConfig();
		globalConfig.setTitle(getTitle());
		config.setGlobal(globalConfig);
		globalConfig.getLogParser().clear();
		parserConfigCatalog.stream().filter(p -> p.isEditable()).forEach(p -> globalConfig.getLogParser().add(p));;
		fileControlPanelList.stream().forEach(fcp -> config.addFileControl(fcp.getConfig()));
		return config;
	}
	
	public void applyConfig(LogTrackerConfig config) {
		GlobalConfig global = config.getGlobal();
		if (global != null) {
			if (global.getTitle() != null)
				setTitle(global.getTitle());
			for (ConfiguredLogParser<?,?> logParser : global.getLogParser()) {
				logParser.setEditable(true);
				parserConfigCatalog.add(logParser);
			}
			parserConfigCatalog.markModelChanged();
		}
		for (FileControl fileControlConfig : config.getFileControl()) {
			FileControlPanel fileControlPanel = addNewFileControl();
			fileControlPanel.applyConfig(fileControlConfig);
		}
	}

	/**
	 * Liefere das globale Konfigurationsobjekt, welches applikationsweite Konfigurationen beinhaltet.
	 * @return das Globale-Konfigurationsobjekt.
	 */
	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

}

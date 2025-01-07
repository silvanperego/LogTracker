package org.sper.logtracker.ui;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import org.sper.logtracker.config.FileControl;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.LogTrackerConfig;
import org.sper.logtracker.config.XMLConfigSupport;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.data.Console;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class LogTracker extends JFrame {

	@Serial
	private static final long serialVersionUID = 1L;
	private static String cfgFile;
	private static final List<LogSource> fnameList = new ArrayList<LogSource>();
	private static final LastAppState lastState = new LastAppState();
	private ToolBar toolBar;
	private CControl control;
	private LogFilePanel logFilePanel;
	private ParserConfigCatalog parserConfigCatalog = new ParserConfigCatalog();
	private List<FileControlPanel> fileControlPanelList = new ArrayList<>();
	private List<FileControlPanel> fileControlPanelInRow = new ArrayList<>();
	private GlobalConfig globalConfig = new GlobalConfig();

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		try {
			if (args.length == 0) {
				cfgFile = lastState.getLastFileName();
			}
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
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public LogTracker() throws IOException, InterruptedException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void initialize() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(LogTracker.class.getResource("/LogTrackerLogo.png")));
		setBounds(100, 100, 1400, 1000);
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
		DefaultSingleCDockable logFileDockable = new DefaultSingleCDockable("OwnLogs", "Log File Reading Errors", logFilePanel);
		control.addDockable(logFileDockable);
		logFileDockable.setLocation(fileControlPanelList.isEmpty() ? CLocation.base().normal() : fileControlPanelList.get(0).getDockableLocation());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (cfgFile != null) {
					lastState.storeLastFileName(cfgFile);
				}
			}
		});
		logFileDockable.setVisible(true);
	}

	FileControlPanel addNewFileControl(Double share) {
		final FileControlPanel fileControlPanel = new FileControlPanel(this, parserConfigCatalog);
		final DefaultMultipleCDockable fileSelectionDockable = new DefaultMultipleCDockable(null, "File Selection", fileControlPanel);
		control.addDockable(fileSelectionDockable);
		if (share != null) {
			fileControlPanelInRow.add(fileControlPanel);
			fileSelectionDockable.setLocation(fileControlPanelInRow.size() > 1 ? CLocation.base().normalSouth(share) : CLocation.base().normal());
		} else
			fileSelectionDockable.setLocation(fileControlPanelList.isEmpty() ? CLocation.base().normal() : fileControlPanelList.get(fileControlPanelList.size() - 1).getDockableLocation());
		fileControlPanelList.add(fileControlPanel);
		fileControlPanel.setParentDockable(fileSelectionDockable);
		fileSelectionDockable.setCloseable(true);
		fileSelectionDockable.setVisible(true);
		fileSelectionDockable.addVetoClosingListener(new CVetoClosingListener() {
			
			@Override
			public void closing(CVetoClosingEvent event) {
				// No special action needed here.
			}
			
			@Override
			public void closed(CVetoClosingEvent event) {
				fileControlPanel.cascadeDelete();
				LogTracker.this.fileControlPanelList.remove(fileControlPanel);
				LogTracker.this.fileControlPanelInRow.remove(fileControlPanel);
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
				FileControlPanel fileControlPanel = addNewFileControl(null);
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
		int nfc = (int)config.getFileControl().stream().filter(fc -> checkForNewRow(fc)).count();
		for (FileControl fileControlConfig : config.getFileControl()) {
			final boolean useNewRow = checkForNewRow(fileControlConfig);
			FileControlPanel fileControlPanel = addNewFileControl(useNewRow ? (double)(nfc - fileControlPanelInRow.size()) / nfc : null);
			fileControlPanel.applyConfig(fileControlConfig);
		}
	}

	private boolean checkForNewRow(FileControl fileControlConfig) {
		FileTypeDescriptor<?, ?> logFileTypeDescriptor = parserConfigCatalog.stream().filter(p -> p.getParserName().equals(fileControlConfig.getParserConfig())).findAny().get().getLogFileTypeDescriptor();
		final boolean useNewRow = logFileTypeDescriptor != ParserConfigCatalog.CORRELATION_DATA_TYPE_DESCRIPTOR;
		return useNewRow;
	}

	/**
	 * Liefere das globale Konfigurationsobjekt, welches applikationsweite Konfigurationen beinhaltet.
	 * @return das Globale-Konfigurationsobjekt.
	 */
	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

}

package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.KeepAliveLogReader;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.proc.CategoryCollection;
import org.sper.logtracker.proc.DataPoint;
import org.sper.logtracker.proc.DataPointFactorizer;
import org.sper.logtracker.proc.MultiPipeCollector;
import org.sper.logtracker.proc.NewPointExtractor;
import org.sper.logtracker.proc.UserDataPoint;
import org.sper.logtracker.proc.UserDataPointFactorizer;
import org.sper.logtracker.scatter.ServiceScatterPlot;
import org.sper.logtracker.scatter.TooltipGenerator;
import org.sper.logtracker.stats.StatsCalculator;
import org.sper.logtracker.stats.StatsCalculator.CategoryExtractor;

public class LogTracker {

	private static String cfgFile;
	private static List<String> fnameList = new ArrayList<String>();
	private JFrame frame;
	private ServiceScatterPlot plot;
	private JTabbedPane tabbedPane;
	private NewPointExtractor newPointExtractor;
	@SuppressWarnings("rawtypes")
	private DataPointFactorizer factorizer;
	private ServiceControlPanel serviceControlPanel;
	private UserPanel userPanel;
	private Configuration configuration = new Configuration();
	private FileControlPanel fileControlPanel;
	private KeepAliveElement terminationPointer;
	private StatsCalculator<DataPoint> serviceStatsCalculator;

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

	final class ApplyControlAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setupDataSeries();
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
			plot.setMaxRange(20.);
		}
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
		serviceControlPanel = new ServiceControlPanel(this);
		tabbedPane.addTab("Services/Filter", null, serviceControlPanel, null);
		configuration.registerModule(serviceControlPanel);
		userPanel = new UserPanel(this);
		userPanel.setVisible(false);
		configuration.registerModule(userPanel);
		plot = new ServiceScatterPlot();
		tabbedPane.addTab("Graph", null, plot.getPanel(), null);
		if (cfgFile != null) {
			try {
				configuration.loadConfiguration(new File(cfgFile));
				fileControlPanel.setupDataPipeLines();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "Error when loading Config-File", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	Configuration getConfiguration() {
		return configuration;
	}

	@SuppressWarnings("unchecked")
	void setupDataPipeLines(List<String> fname, LogParser logParser) {
		try {
			serviceControlPanel.cleanTable();
			factorizer = logParser.providesUsers() ? new UserDataPointFactorizer() : new DataPointFactorizer<DataPoint>();
			serviceStatsCalculator = new StatsCalculator<DataPoint>(factorizer.getService(), new CategoryExtractor<DataPoint>() {

				@Override
				public Integer cat(DataPoint dp) {
					return dp.svcIdx;
				}
			}, serviceControlPanel.getTable(), true, serviceControlPanel.getPublishingSemaphore(), serviceControlPanel.getApplyButton());
			factorizer.addListener(serviceStatsCalculator);
			userPanel.clearTable();
			if (logParser.providesUsers()) {
				if (tabbedPane.getTabCount() < 4)
					tabbedPane.insertTab("Users", null, userPanel, null, 2);
				factorizer.addListener(new StatsCalculator<UserDataPoint>(factorizer.getUser(), new CategoryExtractor<UserDataPoint>() {
					@Override
					public Integer cat(UserDataPoint dp) {
						return dp.userIdx;
					}
				}, userPanel.getTable(), false, null, userPanel.getApplyButon()));
			} else
				if (tabbedPane.getTabCount() == 4)
					tabbedPane.remove(2);
			newPointExtractor = new NewPointExtractor();
			factorizer.addListener(newPointExtractor);
			if (terminationPointer != null)
				terminationPointer.endOfLife();
			Long obsStart = fileControlPanel.getObsStart();
			if (fname.size() == 1) {
				LogLineParser logLineParser = new LogLineParser(logParser, obsStart);
				logLineParser.registerListener(factorizer);
				KeepAliveLogReader keepAliveLogReader = new KeepAliveLogReader(new File(fname.get(0)), logLineParser);
				terminationPointer = keepAliveLogReader;
				keepAliveLogReader.start();
			} else {
				// Bei mehreren Input-Files müssen die Daten durch einen MutliPipeCollector zusammengefasst werden.
				MultiPipeCollector pipeCollector = new MultiPipeCollector();
				pipeCollector.addListener(factorizer);
				for (String fn : fname) {
					LogLineParser logLineParser = new LogLineParser(logParser, obsStart);
					KeepAliveLogReader keepAliveElement = new KeepAliveLogReader(new File(fn), logLineParser);
					pipeCollector.addFeeder(logLineParser, keepAliveElement);
				}
				pipeCollector.run();
				terminationPointer = pipeCollector;
			}

			Factor services = factorizer.getService();
			Factor users = factorizer.getUser();
			XYToolTipGenerator toolTipGenerator = new TooltipGenerator(services, users);
			plot.getXyPlot().getRenderer().setBaseToolTipGenerator(toolTipGenerator);
			tabbedPane.setSelectedIndex(1);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public static void setCfgFile(String cfgFile) {
		LogTracker.cfgFile = cfgFile;
	}

	public void setupDataSeries() {
		CategoryCollection users = null;
		if (((LogParser) fileControlPanel.getLogFileFormatBox().getSelectedItem()).providesUsers())
			users = userPanel.createUsersFilter(((UserDataPointFactorizer)factorizer).getUser());
		newPointExtractor.removeListeners();
		newPointExtractor.addListener(plot);
		serviceControlPanel.applyToSeriesCollection(newPointExtractor, factorizer.getService(), plot.getXyPlot(), users);
		newPointExtractor.resendData();
	}
	
	public void setTabIdx(int idx) {
		tabbedPane.setSelectedIndex(idx);
	}

}

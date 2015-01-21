package org.sper.logtracker.parserconf;

import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;

/**
 * Führt eine Liste aller Verfügbaren Log-File-Parser und ihrer Eigenschaften.
 * @author silvan.perego
 */
public class ParserSelectionModel extends AbstractListModel implements ComboBoxModel {
	
	private static final long serialVersionUID = 1L;
	private ConfiguredLogParser wlsAccessLogParser, tomcatAccessLogParser;	// Diese zwei Log-Parser sind vorkonfiguriert.
	private LogParserList logParserList = new LogParserList();
	private Object selectedItem;
	private ConfiguredLogParser configureItem;
	
	public ParserSelectionModel() {
		prepareAccessLogParsers();
	}

	void saveInSelectionModel(List<ConfiguredLogParser> newLogParser) {
		prepareAccessLogParsers();
		for (ConfiguredLogParser logParser : newLogParser) {
			logParserList.add(logParserList.size() - 1, logParser);
		}
		fireContentsChanged(this, 0, logParserList.size());
	}
	
	private void prepareAccessLogParsers() {
		logParserList.clear();
		wlsAccessLogParser = new ConfiguredLogParser("WebLogic AppServer Access-Log");
		wlsAccessLogParser.setIncludeExcludePattern(Pattern.compile("^#"));
		wlsAccessLogParser.setIncludeLines(false);
		wlsAccessLogParser.setIncludeContaining(true);
		wlsAccessLogParser.setDataExtractionPattern(Pattern.compile("([-\\d]+\\t[:\\d\\.]+)\\t(?:[^\\t]+\\t){3}[-/\\w]*?/([^/\\t?]+)(?:\\?[^\\t]*)?\\t(?:[^\\t]+\\t){2}([\\d\\.]+)"));
		wlsAccessLogParser.setOccTimeFormatString("yyyy-MM-dd\tkk:mm:ss.SSS");
		wlsAccessLogParser.setOccTimeIdx(1);
		wlsAccessLogParser.setServiceIdx(2);
		wlsAccessLogParser.setResponseTimeIdx(3);
		wlsAccessLogParser.setResponseTimeFactor(1.d);
		logParserList.add(wlsAccessLogParser);
		tomcatAccessLogParser = new ConfiguredLogParser("Tomcat \"common\" Access-Log");
		tomcatAccessLogParser.setIncludeExcludePattern(Pattern.compile("-$"));
		tomcatAccessLogParser.setIncludeLines(false);
		tomcatAccessLogParser.setIncludeContaining(true);
		tomcatAccessLogParser.setDataExtractionPattern(Pattern.compile("[\\d\\.]+ \\S (\\S+) \\[(\\S+) \\S+ \"\\w+ (?:/[^/]*)*?/([^/]*?)(?:\\?\\S*)? \\S+ \\d+ (\\d+)"));
		tomcatAccessLogParser.setOccTimeFormatString("dd/MMM/yyyy:kk:mm:ss");
		tomcatAccessLogParser.setOccTimeIdx(2);
		tomcatAccessLogParser.setOccTimeLanguage("en");
		tomcatAccessLogParser.setServiceIdx(3);
		tomcatAccessLogParser.setResponseTimeIdx(4);
		tomcatAccessLogParser.setResponseTimeFactor(0.001d);
		tomcatAccessLogParser.setUserIdx(1);
		logParserList.add(tomcatAccessLogParser);
		// Add a dummy parser, which represents the Parser Config element, at the end of the list
		configureItem = new ConfiguredLogParser("") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void scanLine(String readLine, LogLineParser logLineParser,
					Long obsStart) {
				
			}

			@Override
			public String toString() {
				return "<html><em>Configure Log Parsers...</em></html>";
			}

			@Override
			public boolean providesUsers() {
				return false;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setName(String name) {
			}
			
		};
		logParserList.add(configureItem);
	}

	@Override
	public int getSize() {
		return logParserList.size();
	}

	List<ConfiguredLogParser> getLogParserList() {
		return logParserList;
	}

	@Override
	public LogParser getElementAt(int index) {
		return logParserList.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	public Object getConfigureItem() {
		return configureItem;
	}
	
	public void addParsers(List<ConfiguredLogParser> logParserList) {
		this.logParserList.addParserConfigs(logParserList);
		fireContentsChanged(this, 0, getSize());
	}

}

package org.sper.logtracker.ui;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.DefaultParserProvider;
import org.sper.logtracker.servstat.ServiceResponseLogParser;


public class LogFileTypeCatalog implements DefaultParserProvider {

	private List<ConfiguredLogParser> defaultParserList;
	private ConfiguredLogParser configureItem;
	
	LogFileTypeCatalog() {
		ServiceResponseLogParser wlsAccessLogParser = new ServiceResponseLogParser("WebLogic AppServer Access-Log");
		wlsAccessLogParser.setIncludeExcludePattern(Pattern.compile("^#"));
		wlsAccessLogParser.setIncludeLines(false);
		wlsAccessLogParser.setIncludeContaining(true);
		wlsAccessLogParser.setDataExtractionPattern(Pattern.compile("([-\\d]+\\t[:\\d\\.]+)\\t(?:[^\\t]+\\t){3}[-/\\w]*?/([^/\\t?]+)(?:\\?[^\\t]*)?\\t(?:[^\\t]+\\t){2}([\\d\\.]+)"));
		wlsAccessLogParser.setOccTimeFormatString("yyyy-MM-dd\tkk:mm:ss.SSS");
		wlsAccessLogParser.setOccTimeIdx(1);
		wlsAccessLogParser.setServiceIdx(2);
		wlsAccessLogParser.setResponseTimeIdx(3);
		wlsAccessLogParser.setResponseTimeFactor(1.d);
		ServiceResponseLogParser tomcatAccessLogParser = new ServiceResponseLogParser("Tomcat \"common\" Access-Log");
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
		// Add a dummy parser, which represents the Parser Config element, at the end of the list
		configureItem = new ConfiguredLogParser("", null) {
			
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

			@Override
			protected void extractData(LogLineParser logLineParser,
					Long obsStart, Matcher m) throws ParseException {
			}
			
		};
		defaultParserList = Arrays.asList(wlsAccessLogParser, tomcatAccessLogParser, configureItem);
	}
	
	public ConfiguredLogParser getConfigureItem() {
		return configureItem;
	}
	/* (non-Javadoc)
	 * @see org.sper.logtracker.ui.DefaultParserProvider#prepareDefaultAccessLogParsers()
	 */
	@Override
	public List<ConfiguredLogParser> getDefaultLogParsers() {
		return defaultParserList;
	}


}

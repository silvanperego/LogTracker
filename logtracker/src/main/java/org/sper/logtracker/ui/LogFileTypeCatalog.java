package org.sper.logtracker.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.parserconf.ErrorLogTypeDescriptor;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.DefaultParserProvider;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.parserconf.ServiceResponseFileTypeDescriptor;


public class LogFileTypeCatalog implements DefaultParserProvider {

	private static final ErrorLogTypeDescriptor ERROR_LOG_TYPE_DESCRIPTOR = new ErrorLogTypeDescriptor();
	private static final ServiceResponseFileTypeDescriptor SERVICE_RESPONSE_FILE_TYPE_DESCRIPTOR = new ServiceResponseFileTypeDescriptor();
	private List<ConfiguredLogParser<?>> defaultParserList;
	private ConfiguredLogParser<?> configureItem;
	
	LogFileTypeCatalog() {
		ServiceResponseLogParser.setFileTypeDescriptor(SERVICE_RESPONSE_FILE_TYPE_DESCRIPTOR);
		ErrorLogParser.setFileTypeDescriptor(ERROR_LOG_TYPE_DESCRIPTOR);
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
		ErrorLogParser diagLogParser = new ErrorLogParser("WebLogic Diagnostic Log");
		diagLogParser.setIncludeExcludePattern(Pattern.compile("^\\s*$"));
		diagLogParser.setIncludeLines(false);
		diagLogParser.setIncludeContaining(false);
		diagLogParser.setDataExtractionPattern(Pattern.compile("\\[(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\+\\d{2}:\\d{2}\\] \\[\\S+\\] \\[(\\w+)(?::\\w+)?\\].*?\\[userId: ([^\\]]+)\\] (?:\\[[^\\]]*\\] *){1,7}(.*)"));
		diagLogParser.setOccTimeIdx(1);
		diagLogParser.setOccTimeFormatString("yyyy-MM-dd'T'kk:mm:ss.SSS");
		diagLogParser.setOccTimeLanguage("en");
		diagLogParser.setUserIdIdx(3);
		diagLogParser.setSeverityIdx(2);
		diagLogParser.setMsgIdx(4);
		// Add a dummy parser, which represents the Parser Config element, at the end of the list
		configureItem = new ConfiguredLogParser<Object>("") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void scanLine(FileSnippet lineInFile, LogLineParser<Object> logLineParser,
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
			protected void extractData(LogLineParser<Object> logLineParser,
					Long obsStart, Matcher m, FileSnippet lineInFile) throws ParseException {
			}

			@Override
			public FileTypeDescriptor getLogFileTypeDescriptor() {
				return null;
			}

			@Override
			public String getEncoding() {
				return null;
			}
			
		};
		defaultParserList = new ArrayList<ConfiguredLogParser<?>>();
		defaultParserList.add(wlsAccessLogParser);
		defaultParserList.add(tomcatAccessLogParser);
		defaultParserList.add(diagLogParser);
		defaultParserList.add(configureItem);
	}
	
	public ConfiguredLogParser<?> getConfigureItem() {
		return configureItem;
	}
	/* (non-Javadoc)
	 * @see org.sper.logtracker.ui.DefaultParserProvider#prepareDefaultAccessLogParsers()
	 */
	@Override
	public List<ConfiguredLogParser<?>> getDefaultLogParsers() {
		return defaultParserList;
	}
	
	public List<FileTypeDescriptor> getParserTypeList(ParserConfigDialog dialog) {
		return Arrays.asList(SERVICE_RESPONSE_FILE_TYPE_DESCRIPTOR, ERROR_LOG_TYPE_DESCRIPTOR);
	}

}

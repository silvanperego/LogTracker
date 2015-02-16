package org.sper.logtracker.erroranalysis;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogLineParser;

public class ErrorLogParser extends ConfiguredLogParser {

	private static final long serialVersionUID = 1L;
	public static final String LOG_FILE_TYPE_NAME = "Error Log File";
	private Integer errorLevelIdx;

	public ErrorLogParser(String parserName) {
		super(parserName, LOG_FILE_TYPE_NAME);
	}
	
	public ErrorLogParser(ConfiguredLogParser other) {
		super(other, LOG_FILE_TYPE_NAME);
	}
	
	public Integer getErrorLevelIdx() {
		return errorLevelIdx;
	}

	public void setErrorLevelIdx(Integer errorLevel) {
		this.errorLevelIdx = errorLevel;
	}

	@Override
	public boolean providesUsers() {
		return false;
	}

	@Override
	protected void extractData(LogLineParser logLineParser, Long obsStart,
			Matcher m) throws ParseException {
		
	}

}

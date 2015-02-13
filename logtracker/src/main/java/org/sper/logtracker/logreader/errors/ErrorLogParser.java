package org.sper.logtracker.logreader.errors;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogLineParser;

public class ErrorLogParser extends ConfiguredLogParser {

	private static final long serialVersionUID = 1L;
	private Integer errorLevelIdx;

	public Integer getErrorLevelIdx() {
		return errorLevelIdx;
	}

	public void setErrorLevelIdx(Integer errorLevel) {
		this.errorLevelIdx = errorLevel;
	}

	public ErrorLogParser(String parserName) {
		super(parserName);
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

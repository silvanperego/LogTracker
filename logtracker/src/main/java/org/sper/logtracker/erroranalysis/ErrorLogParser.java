package org.sper.logtracker.erroranalysis;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.sper.logtracker.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

public class ErrorLogParser extends ConfiguredLogParser<RawErrorDataPoint> {

	private static final long serialVersionUID = 1L;
	public static final String LOG_FILE_TYPE_NAME = "Error Log File";
	private Integer severityIdx;
	private Integer userIdIdx;
	private Integer msgIdx;

	public ErrorLogParser(String parserName, FileTypeDescriptor fileTypeDescriptor) {
		super(parserName, fileTypeDescriptor);
	}
	
	public ErrorLogParser(ConfiguredLogParser<?> other, FileTypeDescriptor fileTypeDescriptor) {
		super(other, fileTypeDescriptor);
	}
	
	public Integer getSeverityIdx() {
		return severityIdx;
	}

	public void setSeverityIdx(Integer errorLevel) {
		this.severityIdx = errorLevel;
	}

	@Override
	public boolean providesUsers() {
		return false;
	}

	public Integer getUserIdIdx() {
		return userIdIdx;
	}

	public void setUserIdIdx(Integer userIdIdx) {
		this.userIdIdx = userIdIdx;
	}

	public Integer getMsgIdx() {
		return msgIdx;
	}

	public void setMsgIdx(Integer msgIdx) {
		this.msgIdx = msgIdx;
	}

	@Override
	protected void extractData(LogLineParser<RawErrorDataPoint> logLineParser, Long obsStart,
			Matcher m) throws ParseException {
		
	}

}

package org.sper.logtracker.erroranalysis;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

public class ErrorLogParser extends ConfiguredLogParser<RawErrorDataPoint> {

	private static final long serialVersionUID = 1L;
	public static final String LOG_FILE_TYPE_NAME = "Error Log File";
	private static FileTypeDescriptor fileTypeDescriptor;
	private Integer severityIdx;
	private Integer userIdIdx;
	private Integer msgIdx;
	private ThreadLocal<FileSnippet> lastLineInFile = new ThreadLocal<FileSnippet>();

	public ErrorLogParser(String parserName) {
		super(parserName);
	}
	
	public ErrorLogParser(ConfiguredLogParser<?> other) {
		super(other);
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
	protected void extractData(LogLineParser<RawErrorDataPoint> logLineParser, Long obsStart, Matcher m, FileSnippet fileSnippet) throws ParseException {
		Long time = occTimeIdx != null ? getOccTime(m) : null;
		if (time == null || obsStart == null || time.longValue() > obsStart.longValue()) {
			String msg = m.group(msgIdx);
			String severity = severityIdx != null ? m.group(severityIdx) : null;
			String user = userIdIdx != null ? m.group(userIdIdx) : null;
			logLineParser.receiveData(new RawErrorDataPoint(time, user, severity, msg, fileSnippet));
			if (lastLineInFile.get() != null)
				lastLineInFile.get().setEndPos(fileSnippet);
			lastLineInFile.set(fileSnippet);
		}
	}

	@Override
	public FileTypeDescriptor getLogFileTypeDescriptor() {
		return fileTypeDescriptor;
	}

	public static void setFileTypeDescriptor(FileTypeDescriptor fileTypeDescriptor) {
		ErrorLogParser.fileTypeDescriptor = fileTypeDescriptor;
	}
	
}

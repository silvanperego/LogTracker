package org.sper.logtracker.erroranalysis;

import java.text.ParseException;
import java.util.regex.Matcher;

import javax.xml.bind.annotation.XmlAttribute;

import org.sper.logtracker.correlation.CorrelationLogParser;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

public class ErrorLogParser extends CorrelationLogParser<RawErrorDataPoint> {

	private static final long serialVersionUID = 1L;
	private static FileTypeDescriptor fileTypeDescriptor;
	private Integer severityIdx;
	private Integer msgIdx;
	private String encoding;

	public ErrorLogParser() {
	}
	
	public ErrorLogParser(String parserName) {
		super(parserName);
	}
	
	public ErrorLogParser(ConfiguredLogParser<?> other) {
		super(other);
	}
	
	@Override
	@XmlAttribute
	public String getEncoding() {
		return encoding != null ? encoding : super.getEncoding();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@XmlAttribute
	public Integer getSeverityIdx() {
		return severityIdx;
	}

	public void setSeverityIdx(Integer errorLevel) {
		this.severityIdx = errorLevel;
	}

	@XmlAttribute
	public Integer getMsgIdx() {
		return msgIdx;
	}

	public void setMsgIdx(Integer msgIdx) {
		this.msgIdx = msgIdx;
	}

	@Override
	protected void extractData(LogLineParser<RawErrorDataPoint> logLineParser, Long obsStart, Matcher m, FileSnippet fileSnippet) throws ParseException {
		Long time = getOccTime().getFieldIdx() != null ? getOccTime(m) : null;
		if (time == null || obsStart == null || time.longValue() > obsStart.longValue()) {
			String msg = msgIdx != null ? m.group(msgIdx) : null;
			String severity = severityIdx != null && m.group(severityIdx) != null ? m.group(severityIdx).toUpperCase() : null;
			String user = userIdIdx != null ? m.group(userIdIdx) : null;
			logLineParser.receiveData(new RawErrorDataPoint(time, user, severity, msg, logLineParser.getLogSource(), fileSnippet, getCorrelationId(m)));
			if (lastLineInFile == null)
				lastLineInFile = new ThreadLocal<FileSnippet>();
			if (lastLineInFile.get() != null)
				lastLineInFile.get().setEndPos(fileSnippet);
			lastLineInFile.set(fileSnippet);
		}
	}

	public void setOccTimeTimezone(String occTimeTimezone) {
		getOccTime().setTimezone(occTimeTimezone);
	}

	public static void setFileTypeDescriptor(FileTypeDescriptor fileTypeDescriptor) {
		ErrorLogParser.fileTypeDescriptor = fileTypeDescriptor;
	}

	@Override
	public FileTypeDescriptor getLogFileTypeDescriptor() {
		return fileTypeDescriptor;
	}

}

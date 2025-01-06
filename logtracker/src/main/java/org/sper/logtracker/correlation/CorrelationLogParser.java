package org.sper.logtracker.correlation;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

import java.text.ParseException;
import java.util.regex.Matcher;

public class CorrelationLogParser extends ConfiguredLogParser<CorrelationLogParser, RawCorrelatedDataPoint> {

	private static final long serialVersionUID = 1L;
	private static FileTypeDescriptor<CorrelationLogParser, RawCorrelatedDataPoint> fileTypeDescriptor;
	protected Integer userIdIdx, serviceNameIdx;
	protected transient ThreadLocal<FileSnippet> lastLineInFile = new ThreadLocal<FileSnippet>();
	private String encoding;

	public CorrelationLogParser() {
	}

	public CorrelationLogParser(String parserName) {
		super(parserName);
	}

	public CorrelationLogParser(ConfiguredLogParser<?,?> other) {
		super(other);
		if (other instanceof CorrelationLogParser) {
			CorrelationLogParser otherCorr = (CorrelationLogParser) other;
			userIdIdx = otherCorr.userIdIdx;
		}
	}

	@Override
	@XmlAttribute
	public String getEncoding() {
		return encoding != null ? encoding : super.getEncoding();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public boolean providesUsers() {
		return false;
	}

	@XmlAttribute
	public Integer getUserIdIdx() {
		return userIdIdx;
	}

	public void setUserIdIdx(Integer userIdIdx) {
		this.userIdIdx = userIdIdx;
	}

	public static void setFileTypeDescriptor(FileTypeDescriptor<CorrelationLogParser, RawCorrelatedDataPoint> fileTypeDescriptor) {
		CorrelationLogParser.fileTypeDescriptor = fileTypeDescriptor;
	}

	@Override
	public FileTypeDescriptor<CorrelationLogParser, RawCorrelatedDataPoint> getLogFileTypeDescriptor() {
		return fileTypeDescriptor;
	}

	@Override
	protected void extractData(LogLineParser<RawCorrelatedDataPoint> logLineParser, Long obsStart, Matcher m,
			FileSnippet lineInFile) throws ParseException {
		Long time = getOccTime().getFieldIdx() != null ? getOccTime(m) : null;
		if (time == null || obsStart == null || time.longValue() > obsStart.longValue()) {
			String user = userIdIdx != null ? m.group(userIdIdx) : null;
			String serviceName = serviceNameIdx != null ? m.group(serviceNameIdx) : null;
			logLineParser.receiveData(new RawCorrelatedDataPoint(time, serviceName, user, logLineParser.getLogSource(), getCorrelationId(m), lineInFile));
			if (lastLineInFile == null)
				lastLineInFile = new ThreadLocal<FileSnippet>();
			if (lastLineInFile.get() != null)
				lastLineInFile.get().setEndPos(lineInFile);
			lastLineInFile.set(lineInFile);
		}
	}

	public Integer getServiceNameIdx() {
		return serviceNameIdx;
	}

	@XmlAttribute
	public void setServiceNameIdx(Integer serviceNameIdx) {
		this.serviceNameIdx = serviceNameIdx;
	}

}
package org.sper.logtracker.correlation;

import java.text.ParseException;
import java.util.regex.Matcher;

import javax.xml.bind.annotation.XmlAttribute;

import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;

public class CorrelationLogParser<T extends RawCorrelatedDataPoint> extends ConfiguredLogParser<T> {

	private static final long serialVersionUID = 1L;
	private static FileTypeDescriptor fileTypeDescriptor;
	protected Integer userIdIdx;
	protected transient ThreadLocal<FileSnippet> lastLineInFile = new ThreadLocal<FileSnippet>();

	public CorrelationLogParser() {
		super();
	}

	public CorrelationLogParser(String parserName) {
		super(parserName);
	}

	public CorrelationLogParser(ConfiguredLogParser<?> other) {
		super(other);
		if (other instanceof CorrelationLogParser) {
			CorrelationLogParser<?> otherCorr = (CorrelationLogParser<?>) other;
			userIdIdx = otherCorr.userIdIdx;
		}
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

	public static void setFileTypeDescriptor(FileTypeDescriptor fileTypeDescriptor) {
		CorrelationLogParser.fileTypeDescriptor = fileTypeDescriptor;
	}

	@Override
	public FileTypeDescriptor getLogFileTypeDescriptor() {
		return fileTypeDescriptor;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void extractData(LogLineParser<T> logLineParser, Long obsStart, Matcher m,
			FileSnippet lineInFile) throws ParseException {
		Long time = getOccTime().getFieldIdx() != null ? getOccTime(m) : null;
		if (time == null || obsStart == null || time.longValue() > obsStart.longValue()) {
			String user = userIdIdx != null ? m.group(userIdIdx) : null;
			logLineParser.receiveData((T) new RawCorrelatedDataPoint(time, user, logLineParser.getLogSource(), getCorrelationId(m), lineInFile));
			if (lastLineInFile == null)
				lastLineInFile = new ThreadLocal<FileSnippet>();
		}
	}

}
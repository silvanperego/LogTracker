package org.sper.logtracker.correlation.data;

import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.FileSnippet;

public class RawCorrelatedDataPoint extends RawDataPoint implements CorrelatedMessage {

	public final FileSnippet fileSnippet;

	public RawCorrelatedDataPoint(Long occTime, String user, String logSource, String correlationId, FileSnippet fileSnippet) {
		super(occTime, user, logSource, correlationId);
		this.fileSnippet = fileSnippet;
	}

	@Override
	public String getCorrelationId() {
		return correlationId;
	}

	@Override
	public Long getOccurrenceTime() {
		return occTime;
	}

	@Override
	public String getLogSource() {
		return logSource;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getDescription() {
		return correlationId;
	}

	@Override
	public String getDetail() {
		return fileSnippet.getContents();
	}

}
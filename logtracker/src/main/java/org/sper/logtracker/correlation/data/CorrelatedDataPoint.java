package org.sper.logtracker.correlation.data;

import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogSource;

public class CorrelatedDataPoint implements CorrelatedMessage {

	public final Integer serviceName, user;
	private LogSource logSource;
	private Long occTime;
	private String correlationId;
	private FileSnippet fileSnippet;
	private CorrelationFactors factors;

	public CorrelatedDataPoint(Long occTime, Integer serviceName, Integer user, LogSource logSource, String correlationId,
			FileSnippet fileSnippet, CorrelationFactors factors) {
		this.occTime = occTime;
		this.serviceName = serviceName;
		this.user = user;
		this.logSource = logSource;
		this.correlationId = correlationId;
		this.fileSnippet = fileSnippet;
		this.factors = factors;
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
	public LogSource getLogSource() {
		return logSource != null ? logSource : null;
	}

	@Override
	public String getUser() {
		return user != null ? factors.getUser().getLabel(user) : null;
	}

	@Override
	public String getDescription() {
		return serviceName != null ? factors.getServiceName().getLabel(serviceName) : correlationId;
	}

	@Override
	public String getDetail() {
		return fileSnippet.getContents();
	}

}
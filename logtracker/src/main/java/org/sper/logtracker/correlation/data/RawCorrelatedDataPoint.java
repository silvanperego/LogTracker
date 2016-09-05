package org.sper.logtracker.correlation.data;

import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogSource;

public class RawCorrelatedDataPoint extends RawDataPoint {

	public final FileSnippet fileSnippet;
	public final String serviceName;

	public RawCorrelatedDataPoint(Long occTime, String serviceName, String user, LogSource logSource, String correlationId, FileSnippet fileSnippet) {
		super(occTime, user, logSource, correlationId);
		this.serviceName = serviceName;
		this.fileSnippet = fileSnippet;
	}

}
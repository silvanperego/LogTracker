package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.logreader.LogSource;

/**
 * Ein {@link DataPoint} mit einer Referenz zum {@link StatsDataPointFactorizer}.
 * Er ist in der Lage User und Service mit Klarnamen auszugeben.
 * @author silvan.perego
 */
public class CorrelatedServiceDataPoint extends DataPoint implements CorrelatedMessage {

	private StatsDataPointFactorizer<?> factorizer;

	public CorrelatedServiceDataPoint(Integer svcIdx, Long occTime, Double execTime, Integer user, Integer returnCode,
			LogSource source, String correlationId, StatsDataPointFactorizer<?> factorizer) {
		super(svcIdx, occTime, execTime, user, returnCode, source, correlationId);
		this.factorizer = factorizer;
	}

	private static final long serialVersionUID = 1L;

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
		return logSource;
	}

	@Override
	public String getUser() {
		return user != null ? factorizer.getUser().getLabel(user) : null;
	}

	@Override
	public String getDescription() {
		return svcIdx != null ? factorizer.getService().getLabel(svcIdx) : null;
	}

	@Override
	public String getDetail() {
		return null;
	}

	@Override
	public Double getResponseTime() {
		return responseTime;
	}

}

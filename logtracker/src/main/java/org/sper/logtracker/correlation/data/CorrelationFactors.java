package org.sper.logtracker.correlation.data;

import org.sper.logtracker.data.Factor;

public class CorrelationFactors {
	private Factor serviceName = new Factor(), user = new Factor(), logSource = new Factor();

	public Factor getServiceName() {
		return serviceName;
	}

	public Factor getUser() {
		return user;
	}

	public Factor getLogSource() {
		return logSource;
	}
}
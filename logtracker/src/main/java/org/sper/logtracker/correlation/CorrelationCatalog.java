package org.sper.logtracker.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sper.logtracker.data.DataListener;

/**
 * Der Correlation-Catalog erfasst alle Meldungen, welche
 * @author silvan.perego
 *
 */
public class CorrelationCatalog<T extends CorrelatedMessage> implements DataListener<T> {

	@SuppressWarnings("rawtypes")
	private static final CorrelationCatalog INSTANCE = new CorrelationCatalog();
	private Map<String, List<CorrelatedMessage>> correlationMap = new ConcurrentHashMap<>(10000);
	
	@SuppressWarnings("unchecked")
	public static <T extends CorrelatedMessage> CorrelationCatalog<T> getInstance() {
		return INSTANCE;
	}

	@Override
	public void receiveData(CorrelatedMessage data) {
		List<CorrelatedMessage> msgList = correlationMap.computeIfAbsent(data.getCorrelationId(), m -> new ArrayList<>());
		synchronized (msgList) {
			msgList.add(data);
		}
	}

	@Override
	public void publishData() {
	}
	
}

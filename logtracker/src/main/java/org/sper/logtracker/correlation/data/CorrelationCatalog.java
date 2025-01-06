package org.sper.logtracker.correlation.data;

import org.sper.logtracker.data.DataListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Der Correlation-Catalog erfasst alle Meldungen, welche
 * @author silvan.perego
 *
 */
public class CorrelationCatalog<T extends CorrelatedMessage> implements DataListener<T> {

	@SuppressWarnings("rawtypes")
	private static final CorrelationCatalog INSTANCE = new CorrelationCatalog();
	private Map<String, List<T>> correlationMap = new ConcurrentHashMap<>(10000);
	
	@SuppressWarnings("unchecked")
	public static <T extends CorrelatedMessage> CorrelationCatalog<T> getInstance() {
		return INSTANCE;
	}

	@Override
	public void receiveData(T data) {
		List<T> msgList = correlationMap.computeIfAbsent(data.getCorrelationId(), m -> new ArrayList<>());
		synchronized (msgList) {
			msgList.add(data);
		}
	}

	@Override
	public void publishData() {
	}
	
	public List<T> getMessagesForCategory(String correlationId) {
		List<T> msgList = correlationMap.get(correlationId);
		if (msgList != null) {
			List<T> result;
			synchronized (msgList) {
				result = new ArrayList<>(msgList);
			}
			Collections.sort(result, (a, b) -> a.getOccurrenceTime().compareTo(b.getOccurrenceTime()));
			return result;
		}
		return Collections.emptyList();
	}
	
}

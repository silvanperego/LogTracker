package org.sper.logtracker.logreader.servstat;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.proc.RawDataPoint;

public class ServiceResponseLogParser extends ConfiguredLogParser {

	private static final long serialVersionUID = 1L;
	private String ignoreServiceList;
	private int serviceIdx;
	private int responseTimeIdx;
	private Integer userIdx;
	private double responseTimeFactor = 1.d;
	public ServiceResponseLogParser(String parserName) {
		super(parserName);
	}

	/**
	 * @param ignoreServiceList Eine (komma-)separierte Liste der Services, welche von vorneweg ignoriert werden sollen. Dies ist <em>kein</em> Regex-Pattern!
	 */
	public void setIgnoreServiceList(String ignoreServiceList) {
		this.ignoreServiceList = ignoreServiceList;
	}

	/**
	 * @param serviceIdx Der Gruppen-Index der Regex-Gruppe welche den Service-Namen liefert.
	 */
	public void setServiceIdx(int serviceIdx) {
		this.serviceIdx = serviceIdx;
	}

	/**
	 * @param responseTimeIdx Der Gruppen-Index der Regex-Gruppe welche die Antwortzeit liefert.
	 */
	public void setResponseTimeIdx(int responseTimeIdx) {
		this.responseTimeIdx = responseTimeIdx;
	}

	/**
	 * @param userIdx Der Gruppen-Index der Regex-Gruppe welche den User liefert. Dies ist die einzige Gruppen-Index, welche
	 * leergelassen werden darf.
	 */
	public void setUserIdx(Integer userIdx) {
		this.userIdx = userIdx;
	}

	/**
	 * @param responseTimeFactor Setzt einen Multiplikationsfaktor für die Response-Zeiten. Dieser Faktor dient dazu,
	 * die Zeitangaben im Log-File nach Sekunden zu konvertieren.
	 */
	public void setResponseTimeFactor(double responseTimeFactor) {
		this.responseTimeFactor = responseTimeFactor;
	}

	public String getIgnoreServiceList() {
		return ignoreServiceList;
	}

	public int getServiceIdx() {
		return serviceIdx;
	}

	public int getResponseTimeIdx() {
		return responseTimeIdx;
	}

	public Integer getUserIdx() {
		return userIdx;
	}

	public double getResponseTimeFactor() {
		return responseTimeFactor;
	}

	@Override
	protected void extractData(LogLineParser logLineParser, Long obsStart, Matcher m) throws ParseException {
		String service = m.group(serviceIdx);
		if (service.isEmpty())
			service = "/";
		if (ignoreServiceList == null || ignoreServiceList.indexOf(service) < 0) {
			long time = getOccTime(m);
			if (obsStart == null || time >= obsStart) {
				double execTime = Double.parseDouble(m.group(responseTimeIdx)) * responseTimeFactor;
				String user = userIdx != null ? m.group(userIdx) : null;
				logLineParser.receiveData(new RawDataPoint(time, execTime, service, user));
			}
		}
	}

	@Override
	public boolean providesUsers() {
		return userIdx != null;
	}

}

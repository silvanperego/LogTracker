package org.sper.logtracker.servstat;

import java.text.ParseException;
import java.util.regex.Matcher;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.sper.logtracker.data.Console;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;

@XmlType(name = "ServiceResponseLogParser")
public class ServiceResponseLogParser extends ConfiguredLogParser<ServiceResponseLogParser, RawStatsDataPoint> {

	private static final long serialVersionUID = 1L;
	transient static FileTypeDescriptor<ServiceResponseLogParser, RawStatsDataPoint> fileTypeDescriptor;
	private String ignoreServiceList;
	private int serviceIdx;
	private int responseTimeIdx;
	private Integer userIdx;
	private double responseTimeFactor = 1.d;
	private Integer successCode;
	private Integer returnCodeIdx;
	private boolean substract;
	
	public ServiceResponseLogParser() {
	}
	
	public ServiceResponseLogParser(String parserName) {
		super(parserName);
	}
	
	public ServiceResponseLogParser(ConfiguredLogParser<?,?> other) {
		super(other);
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

	@XmlElement
	public String getIgnoreServiceList() {
		return ignoreServiceList;
	}

	@XmlAttribute
	public int getServiceIdx() {
		return serviceIdx;
	}

	@XmlAttribute
	public int getResponseTimeIdx() {
		return responseTimeIdx;
	}

	@XmlAttribute
	public Integer getUserIdx() {
		return userIdx;
	}

	@XmlAttribute
	public double getResponseTimeFactor() {
		return responseTimeFactor;
	}

	@Override
	protected void extractData(LogLineParser<RawStatsDataPoint> logLineParser, Long obsStart, Matcher m, FileSnippet lineInFile) throws ParseException {
		String service = m.group(serviceIdx);
		if (service.isEmpty())
			service = "/";
		if (ignoreServiceList == null || ignoreServiceList.indexOf(service) < 0) {
			long time = getOccTime(m);
			if (obsStart == null || time >= obsStart) {
				double execTime = Double.parseDouble(m.group(responseTimeIdx)) * responseTimeFactor;
				String user = userIdx != null ? m.group(userIdx) : null;
				Integer returnCode = returnCodeIdx != null && m.group(returnCodeIdx) != null ? Integer.valueOf(m.group(returnCodeIdx)) : null;
				logLineParser.receiveData(new RawStatsDataPoint(time, execTime, service, user, returnCode, logLineParser.getLogSource(), getCorrelationId(m)));
				if (substract)
					time -= 1000. * execTime;
			}
		}
	}

	@Override
	public boolean providesUsers() {
		return userIdx != null;
	}

	@Override
	protected void markUnknownLine(String readLine) {
		StringBuilder sb = new StringBuilder();
		sb.append("Warning: Line with unknown format: ");
		sb.append(readLine);
		sb.append('\n');
		Console.addMessage(sb.toString());
	}

	@Override
	public FileTypeDescriptor<ServiceResponseLogParser, RawStatsDataPoint> getLogFileTypeDescriptor() {
		return fileTypeDescriptor;
	}

	public static void setFileTypeDescriptor(FileTypeDescriptor<ServiceResponseLogParser, RawStatsDataPoint> fileTypeDescriptor) {
		ServiceResponseLogParser.fileTypeDescriptor = fileTypeDescriptor;
	}

	@XmlAttribute
	public Integer getReturnCodeIdx() {
		return returnCodeIdx;
	}

	@XmlAttribute
	public Integer getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(Integer successCode) {
		this.successCode = successCode;
	}

	public void setReturnCodeIdx(Integer returnCodeIdx) {
		this.returnCodeIdx = returnCodeIdx;
	}

	public void setSubstract(boolean selected) {
		this.substract = selected;
	}

	@XmlAttribute
	public boolean isSubstract() {
		return substract;
	}
	
}

package org.sper.logtracker.logreader;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sper.logtracker.data.Console;
import org.sper.logtracker.proc.RawDataPoint;

/**
 * Ein generischer Log-Parser, welcher über Konfigurationsparamter auf eine Vielzahl von Log-File-Formaten eingestellt werden kann.
 * @author silvan.perego
 */
public class ConfiguredLogParser implements LogParser, Serializable, Cloneable {

	public static final String CONFIG_NAME = "ParserConfig";
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat occTimeFormatString = null;
	private Pattern linePattern, includeExcludePattern;
	private String ignoreServiceList, occTimeLanguage;
	private int occTimeIdx, serviceIdx, responseTimeIdx;
	private Integer userIdx;
	private double responseTimeFactor = 1.d;
	private String parserName;
	private boolean includeLines, includeContaining, editable;
	private String dateFormat;
	
	
	public ConfiguredLogParser(String parserName) {
		this.parserName = parserName;
	}
	
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	/**
	 * @param dateFormat Das Format, mit welchem der occurrence-Time Zeitstempel aus dem Log-File gelesen wird.
	 * Es entspricht den Regeln für {@link SimpleDateFormat}.
	 */
	public void setOccTimeFormatString(String dateFormat) {
		this.dateFormat = dateFormat;
		occTimeFormatString = null;
	}

	/**
	 * @param linePattern dieses Pattern dient der Identfikation der Werte, welche aus der Log-Zeile extrahiert werden müssen. Die Werte
	 * 	müssen als Regex-Gruppen vorliegen.
	 */
	public void setDataExtractionPattern(Pattern linePattern) {
		this.linePattern = linePattern;
	}

	/**
	 * @param includeExcludePattern Mit diesem Pattern können Zeilen von vorneweg ignoriert oder aber berücksichtigt werden. (Siehe auch
	 * {@link #setInclusionOperator(InclusionOperator)}.)
	 */
	public void setIncludeExcludePattern(Pattern includeExcludePattern) {
		this.includeExcludePattern = includeExcludePattern;
	}

	/**
	 * @param ignoreServiceList Eine (komma-)separierte Liste der Services, welche von vorneweg ignoriert werden sollen. Dies ist <em>kein</em> Regex-Pattern!
	 */
	public void setIgnoreServiceList(String ignoreServiceList) {
		this.ignoreServiceList = ignoreServiceList;
	}

	/**
	 * @param occTimeIdx Der Gruppen-Index der Regex-Gruppe welche die Occurrence-Time liefert.
	 */
	public void setOccTimeIdx(int occTimeIdx) {
		this.occTimeIdx = occTimeIdx;
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

	@Override
	synchronized public void scanLine(String readLine, LogLineParser logLineParser, Long obsStart) {
		if (occTimeFormatString == null) {
			this.occTimeFormatString = 
					occTimeLanguage != null && !occTimeLanguage.isEmpty() ? 
							new SimpleDateFormat(dateFormat, new Locale(occTimeLanguage)) :
							new SimpleDateFormat(dateFormat);
		}
		Matcher incExlMatcher = includeExcludePattern.matcher(readLine);
		boolean found = includeContaining ?	incExlMatcher.find() : incExlMatcher.matches();
		if (found == includeLines) {
			Matcher m = linePattern.matcher(readLine);
			if (m.matches()) {
				try {
					String service = m.group(serviceIdx);
					if (service.isEmpty())
						service = "/";
					if (ignoreServiceList == null || ignoreServiceList.indexOf(service) < 0) {
						long time = occTimeFormatString.parse(m.group(occTimeIdx)).getTime();
						if (obsStart == null || time >= obsStart) {
							double execTime = Double.parseDouble(m.group(responseTimeIdx)) * responseTimeFactor;
							String user = userIdx != null ? m.group(userIdx) : null;
							logLineParser.receiveData(new RawDataPoint(time, execTime, service, user));
						}
					}
				} catch (Exception e) {
					Console.addMessage(e.toString());
					throw new RuntimeException(e);
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Warning: Line with unknown format: ");
				sb.append(readLine);
				sb.append('\n');
				Console.addMessage(sb.toString());
			}
		}
	}

	@Override
	public String toString() {
		return parserName;
	}
	
	@Override
	public boolean providesUsers() {
		return userIdx != null;
	}

	@Override
	public String getName() {
		return parserName;
	}
	
	@Override
	public void setName(String name) {
		parserName = name;
	}

	public String getOccTimeFormatString() {
		return dateFormat;
	}

	public Pattern getLinePattern() {
		return linePattern;
	}
	
	public void setLinePattern(Pattern linePattern) {
		this.linePattern = linePattern;
	}
	
	public String getParserName() {
		return parserName;
	}
	
	public void setParserName(String parserName) {
		this.parserName = parserName;
	}
	
	public Pattern getIncludeExcludePattern() {
		return includeExcludePattern;
	}
	
	public String getIgnoreServiceList() {
		return ignoreServiceList;
	}
	
	public int getOccTimeIdx() {
		return occTimeIdx;
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
	
	public String getOccTimeLanguage() {
		return occTimeLanguage;
	}

	public void setOccTimeLanguage(String dateLanguage) {
		this.occTimeLanguage = dateLanguage;
		occTimeFormatString = null;
	}

	public boolean isIncludeLines() {
		return includeLines;
	}
	public void setIncludeLines(boolean includeLines) {
		this.includeLines = includeLines;
	}
	public boolean isIncludeContaining() {
		return includeContaining;
	}
	public void setIncludeContaining(boolean includeContaining) {
		this.includeContaining = includeContaining;
	}
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

}

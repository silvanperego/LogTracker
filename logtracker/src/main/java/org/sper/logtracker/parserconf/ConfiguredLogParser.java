package org.sper.logtracker.parserconf;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sper.logtracker.data.Console;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;

/**
 * Ein generischer Log-Parser, welcher über Konfigurationsparameter auf eine Vielzahl von Log-File-Formaten eingestellt werden kann.
 * @author silvan.perego
 */
public abstract class ConfiguredLogParser<T> implements LogParser<T>, Serializable, Cloneable {

	public static final String CONFIG_NAME = "ParserConfig";
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat occTimeFormatString = null;
	private Pattern linePattern, includeExcludePattern;
	private String parserName;
	private boolean includeLines, includeContaining, editable;
	
	protected String occTimeLanguage;
	protected Integer occTimeIdx;
	protected String dateFormat;
	
	public ConfiguredLogParser(String parserName) {
		this.parserName = parserName;
	}
	
	/**
	 * Erstellt einen neuen ConfiguredLogParser und übernimmt die wichtigsten Einträge
	 * @param orig
	 */
	public ConfiguredLogParser(ConfiguredLogParser<?> orig) {
		linePattern = orig.linePattern;
		includeExcludePattern = orig.includeExcludePattern;
		parserName = orig.parserName;
		includeLines = orig.includeLines;
		includeContaining = orig.includeContaining;
		editable = orig.editable;
		occTimeLanguage = orig.occTimeLanguage;
		occTimeIdx = orig.occTimeIdx;
		dateFormat = orig.dateFormat;
	}
	
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	/**
	 * @param linePattern dieses Pattern dient der Identifikation der Werte, welche aus der Log-Zeile extrahiert werden müssen. Die Werte
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

	@Override
	synchronized public void scanLine(FileSnippet lineInFile, LogLineParser<T> logLineParser, Long obsStart) {
		if (occTimeFormatString == null) {
			this.occTimeFormatString = 
					occTimeLanguage != null && !occTimeLanguage.isEmpty() ? 
							new SimpleDateFormat(dateFormat, new Locale(occTimeLanguage)) :
								new SimpleDateFormat(dateFormat);
		}
		String readLine = lineInFile.line;
		Matcher incExlMatcher = includeExcludePattern.matcher(readLine);
		boolean found = includeContaining ?	incExlMatcher.find() : incExlMatcher.matches();
		if (found == includeLines) {
			Matcher m = linePattern.matcher(readLine);
			if (m.matches()) {
				try {
					extractData(logLineParser, obsStart, m, lineInFile);
				} catch (Exception e) {
					Console.addMessage(e.toString());
					throw new RuntimeException(e);
				}
			} else {
				markUnknownLine(readLine);
			}
		}
	}

	protected void markUnknownLine(String readLine) {
	}

	public long getOccTime(Matcher m) throws ParseException {
		return occTimeFormatString.parse(m.group(occTimeIdx)).getTime();
	}

	protected abstract void extractData(LogLineParser<T> logLineParser, Long obsStart,
			Matcher m, FileSnippet lineInFile) throws ParseException;

	@Override
	public String toString() {
		return parserName;
	}
	
	@Override
	public String getName() {
		return parserName;
	}
	
	@Override
	public void setName(String name) {
		parserName = name;
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

	/**
	 * @param dateFormat Das Format, mit welchem der occurrence-Time Zeitstempel aus dem Log-File gelesen wird.
	 * Es entspricht den Regeln für {@link SimpleDateFormat}.
	 */
	public void setOccTimeFormatString(String dateFormat) {
		this.dateFormat = dateFormat;
		occTimeFormatString = null;
	}

	/**
	 * @param occTimeIdx Der Gruppen-Index der Regex-Gruppe welche die Occurrence-Time liefert.
	 */
	public void setOccTimeIdx(int occTimeIdx) {
		this.occTimeIdx = occTimeIdx;
	}

	public String getOccTimeFormatString() {
		return dateFormat;
	}

	public int getOccTimeIdx() {
		return occTimeIdx;
	}

	public String getOccTimeLanguage() {
		return occTimeLanguage;
	}

	public void setOccTimeLanguage(String dateLanguage) {
		this.occTimeLanguage = dateLanguage;
		occTimeFormatString = null;
	}

	public abstract FileTypeDescriptor getLogFileTypeDescriptor();
}

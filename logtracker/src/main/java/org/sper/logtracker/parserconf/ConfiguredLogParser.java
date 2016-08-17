package org.sper.logtracker.parserconf;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.sper.logtracker.data.Console;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;

/**
 * Ein generischer Log-Parser, welcher über Konfigurationsparameter auf eine Vielzahl von Log-File-Formaten eingestellt werden kann.
 * @author silvan.perego
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ConfiguredLogParser<T> implements LogParser<T>, Serializable, Cloneable {

	public static final String CONFIG_NAME = "ParserConfig";
	private static final long serialVersionUID = 1L;

	private Pattern linePattern, includeExcludePattern;
	private String parserName;
	private boolean includeLines, includeContaining, editable;
	private Long timezoneOffset;
	private OccTimeFieldDescription occTime;

	// Die nachfolgenden Felder sind nur vorhanden um die Rückwärtskompatibilität der Config-Files sicherzustellen.
	private SimpleDateFormat occTimeFormatString = null;
	private String occTimeLanguage, occTimeTimezone;
	private Integer occTimeIdx;
	private String dateFormat;

	public static class OccTimeFieldDescription {
		public OccTimeFieldDescription() {
		}
		
		public OccTimeFieldDescription(OccTimeFieldDescription orig) {
			formatString = orig.formatString;
			dateFormat = orig.dateFormat;
			fieldIdx = orig.fieldIdx;
			language = orig.language;
			timezone = orig.timezone;
		}
		
		private SimpleDateFormat formatString = null;
		protected String language, timezone;
		protected Integer fieldIdx;
		protected String dateFormat;
		
		/**
		 * @param dateFormat Das Format, mit welchem der occurrence-Time Zeitstempel aus dem Log-File gelesen wird.
		 * Es entspricht den Regeln für {@link SimpleDateFormat}.
		 */
		public void setFormatString(String dateFormat) {
			this.dateFormat = dateFormat;
			formatString = null;
		}

		/**
		 * @param occTimeIdx Der Gruppen-Index der Regex-Gruppe welche die Occurrence-Time liefert.
		 */
		public void setFieldIdx(Integer occTimeIdx) {
			this.fieldIdx = occTimeIdx;
		}

		@XmlElement
		public String getFormatString() {
			return dateFormat;
		}

		@XmlAttribute
		public Integer getFieldIdx() {
			return fieldIdx;
		}

		@XmlAttribute
		public String getLanguage() {
			return language;
		}

		public void setLanguage(String dateLanguage) {
			this.language = dateLanguage;
			formatString = null;
		}

		@XmlAttribute
		public String getTimezone() {
			return timezone;
		}
		
		public void setTimezone(String occTimeTimezone) {
			this.timezone = occTimeTimezone;
		}

		public String getDateFormat() {
			return dateFormat;
		}
	}
	
	public ConfiguredLogParser() {
		occTime = new OccTimeFieldDescription();
	}
	
	public ConfiguredLogParser(String parserName) {
		this();
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
		occTime = new OccTimeFieldDescription(orig.occTime);
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

	/**
	 * @param includeExcludePattern Mit diesem Pattern können Zeilen von vorneweg ignoriert oder aber berücksichtigt werden. (Siehe auch
	 * {@link #setInclusionOperator(InclusionOperator)}.)
	 */
	@XmlElement(name = "IncludeExcludePattern")
	public String getIncludeExcludePatternStr() {
		return includeExcludePattern.pattern();
	}
	
	/**
	 * @param includeExcludePattern Mit diesem Pattern können Zeilen von vorneweg ignoriert oder aber berücksichtigt werden. (Siehe auch
	 * {@link #setInclusionOperator(InclusionOperator)}.)
	 */
	public void setIncludeExcludePattern(String includeExcludePatternStr) {
		this.includeExcludePattern = Pattern.compile(includeExcludePatternStr);
	}
	
	@Override
	synchronized public void scanLine(FileSnippet lineInFile, LogLineParser<T> logLineParser, Long obsStart) {
		if (getOccTime().formatString == null) {
			this.occTime.formatString = 
					occTime.language != null && !occTime.language.isEmpty() ? 
							new SimpleDateFormat(occTime.dateFormat, new Locale(occTime.language)) :
								new SimpleDateFormat(occTime.dateFormat);
		}
		if (timezoneOffset == null && occTime.timezone != null) {
			long time = System.currentTimeMillis();
			timezoneOffset = (long) (TimeZone.getDefault().getOffset(time) - TimeZone.getTimeZone(occTime.timezone).getOffset(time));
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
		long time = getOccTime().formatString.parse(m.group(getOccTime().fieldIdx)).getTime();
		if (timezoneOffset != null)
			time += timezoneOffset.longValue();
		return time;
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
	
	@XmlElement(name = "linePattern")
	public String getLinePatternStr() {
		return linePattern != null ? linePattern.pattern() : null;
	}
	
	public void setLinePattern(Pattern linePattern) {
		this.linePattern = linePattern;
	}
	
	public void setLinePatternStr(String linePatternStr) {
		this.linePattern = Pattern.compile(linePatternStr);
	}
	
	@XmlAttribute
	public String getParserName() {
		return parserName;
	}
	
	public void setParserName(String parserName) {
		this.parserName = parserName;
	}
	
	public Pattern getIncludeExcludePattern() {
		return includeExcludePattern;
	}
	
	@Override
	public String getEncoding() {
		return "ISO-8859-1";
	}

	@XmlAttribute
	public boolean isIncludeLines() {
		return includeLines;
	}
	public void setIncludeLines(boolean includeLines) {
		this.includeLines = includeLines;
	}

	@XmlAttribute
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


	public abstract FileTypeDescriptor getLogFileTypeDescriptor();

	@XmlElement
	public OccTimeFieldDescription getOccTime() {
		if (occTime == null) {
			// Möglicherweise wurde ein alter Config-File geladen.
			occTime = new OccTimeFieldDescription();
			occTime.dateFormat = dateFormat;
			occTime.fieldIdx = occTimeIdx;
			occTime.formatString = occTimeFormatString;
			occTime.language = occTimeLanguage;
			occTime.timezone = occTimeTimezone;
		}
		return occTime;
	}

	public void setOccTime(OccTimeFieldDescription occTime) {
		this.occTime = occTime;
	}

}

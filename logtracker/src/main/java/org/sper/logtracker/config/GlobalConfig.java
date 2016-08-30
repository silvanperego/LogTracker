package org.sper.logtracker.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.sper.logtracker.correlation.CorrelationLogParser;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.servstat.ServiceResponseLogParser;

/**
 * 
 * @author silvan.perego
 *
 */
public class GlobalConfig {

	private String title;
	private List<ConfiguredLogParser<?,?>> logParser = new ArrayList<>();
	private String timestampFormatStr;

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElements(
			{@XmlElement(name = "ServiceResponseLogParser", type = ServiceResponseLogParser.class),
			@XmlElement(name = "ErrorLogParser", type = ErrorLogParser.class),
			@XmlElement(name ="CorrelationLogParser", type = CorrelationLogParser.class)}
	)
	public List<ConfiguredLogParser<?,?>> getLogParser() {
		return logParser;
	}
	
	public void addLogParser(ConfiguredLogParser<?,?> logParser) {
		this.logParser.add(logParser);
	}

	@XmlElement
	public String getTimestampFormatStr() {
		return timestampFormatStr != null ? timestampFormatStr : "yyyy-MM-dd kk:mm:ss.SSS";
	}

	public void setTimestampFormatStr(String timestampFormatStr) {
		this.timestampFormatStr = timestampFormatStr;
	}
	
}

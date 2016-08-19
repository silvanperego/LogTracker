package org.sper.logtracker.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.servstat.ServiceResponseLogParser;

/**
 * 
 * @author silvan.perego
 *
 */
public class Global {

	private String title;
	private List<ConfiguredLogParser<?,?>> logParser = new ArrayList<>();

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElements(
			{@XmlElement(name = "ServiceResponseLogParser", type = ServiceResponseLogParser.class),
			@XmlElement(name = "ErrorLogParser", type = ErrorLogParser.class)}
	)
	public List<ConfiguredLogParser<?,?>> getLogParser() {
		return logParser;
	}
	
	public void addLogParser(ConfiguredLogParser<?,?> logParser) {
		this.logParser.add(logParser);
	}
	
}

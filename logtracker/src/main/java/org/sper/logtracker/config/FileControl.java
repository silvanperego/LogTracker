package org.sper.logtracker.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.sper.logtracker.logreader.LogSource;

public class FileControl {

	private List<LogSource> logSource = new ArrayList<>();
	private Integer obsVal;
	private String parserConfig;

	@XmlElement
	public List<LogSource> getLogSource() {
		return logSource;
	}
	public void addLogSource(LogSource logSource) {
		this.logSource.add(logSource);
	}
	@XmlAttribute
	public Integer getObsVal() {
		return obsVal;
	}
	public void setObsVal(Integer obsVal) {
		this.obsVal = obsVal;
	}
	@XmlAttribute
	public String getParserConfig() {
		return parserConfig;
	}
	public void setParserConfig(String parserConfig) {
		this.parserConfig = parserConfig;
	}

}

package org.sper.logtracker.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.servstat.ui.ServiceControlPanel.ServiceControlData;

public class FileControl {

	private List<LogSource> logSource = new ArrayList<>();
	private Integer obsVal;
	private String parserConfig;
	private Object controlData;

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
	
	@XmlElements(value = {@XmlElement(name = "ServiceControlData", type = ServiceControlData.class)})
	public Object getControlData() {
		return controlData;
	}
	public void setControlData(Object controlData) {
		this.controlData = controlData;
	}

}

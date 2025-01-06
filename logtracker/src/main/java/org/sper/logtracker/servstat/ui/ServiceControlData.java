package org.sper.logtracker.servstat.ui;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import org.sper.logtracker.servstat.ui.ServiceControlPanel.ServiceControlDataRow;

import java.util.ArrayList;
import java.util.List;

public class ServiceControlData {
	private List<ServiceControlDataRow> controlData = new ArrayList<>();
	private List<String> userExclude = new ArrayList<>();
	private Double magFact;

	@XmlElement
	public List<ServiceControlDataRow> getControlData() {
		return controlData;
	}
	
	public void addControlData(ServiceControlDataRow row) {
		controlData.add(row);
	}

	@XmlAttribute
	public Double getMagFact() {
		return magFact;
	}

	public void setMagFact(Double magFact) {
		this.magFact = magFact;
	}

	@XmlElement
	public List<String> getUserExclude() {
		return userExclude;
	}
	
	public void addUserExclude(String exclude) {
		userExclude.add(exclude);
	}
}
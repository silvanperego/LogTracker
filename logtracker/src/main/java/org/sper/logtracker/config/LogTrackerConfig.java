package org.sper.logtracker.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "", propOrder = {
    "global"
})
@XmlRootElement(name = "LogTrackerConfig")
public class LogTrackerConfig {

	protected Global global;

	public Global getGlobal() {
		return global;
	}

	public void setGlobal(Global global) {
		this.global = global;
	}
	
}

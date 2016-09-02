package org.sper.logtracker.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LogTrackerConfig")
public class LogTrackerConfig {

	private GlobalConfig global;
	private List<FileControl> fileControl = new ArrayList<>();

	@XmlElement
	public GlobalConfig getGlobal() {
		return global;
	}

	public void setGlobal(GlobalConfig global) {
		this.global = global;
	}

	@XmlElement
	public List<FileControl> getFileControl() {
		return fileControl;
	}

	public void addFileControl(FileControl fileControl) {
		this.fileControl.add(fileControl);
	}

}

package org.sper.logtracker.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author silvan.perego
 *
 */
public class Global {

	private String title;

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}

package org.sper.logtracker.ui;

import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

public class WelcomePanel extends JEditorPane {

	private static final long serialVersionUID = 1L;

	private static final String WELCOME_MESSAGE = 
			"<html><body><h1 align='center'>About Log-Tracker</h1>"
			+ "<p align='center'>Version 1.0</p>"
			+ "<p>Log-Tracker is an easy to use tool for monitoring application server access log files. "
			+ "It records execution time, duration, service names and users and displays that data in a"
			+ "configurable chart.</p>"
			+ "<p>Log-Tracker actively monitors the configures log-files and updates the charts and statistics"
			+ "whenever a new data is added.</p>"
			+ "<p>Just type the plus button to add log-files to the monitor, choose an appropriate parser and press \"Apply\"."
			+ "Log-Tracker will show you basic staistics about the services that have been called. Choose the entities that"
			+ "you want to display on the chart and press \"Apply\" again. The chart will show up immediately."
			+ "</p>"
			+ "<p>Copyright 2015 Silvan Perego</p>"
			+ "Licensed under the Apache License, Version 2.0 (the \"License\")"
			+ "you may not use this file except in compliance with the License. "
			+ "You may obtain a copy of the License at <p>"
			+ "<a href='http://www.apache.org/licenses/LICENSE-2.0'>http://www.apache.org/licenses/LICENSE-2.0</a><p>"
			+ "Unless required by applicable law or agreed to in writing, software"
			+ "distributed under the License is distributed on an \"AS IS\" BASIS,"
			+ "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
			+ "See the License for the specific language governing permissions and limitations "
			+ "under the License.<p>"
			+ "This program uses"
			+ "<ul><li><a href='http://www.jfree.org/jfreechart/'>JFreeChart</a> from "
			+ "<a href='http://www.object-refinery.com/'>Object Refinery</a></li>"
			+ "<li><a href='http://launch4j.sourceforge.net/'>Launch4J</a></li>"
			+ "<li><a href='https://github.com/axet/desktop'>Axet Desktop</a></li></ul>"
			+ "</body></html>";
	
	private Desktop desktop = Desktop.getDesktop();

	public WelcomePanel() {
		super("text/html", WELCOME_MESSAGE);
		setEditable(false);
		setPreferredSize(new Dimension(560, 660));
		setAutoscrolls(true);
		addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				try {
					if (e.getEventType() == EventType.ACTIVATED)
						desktop.browse(e.getURL().toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
}

package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.JPanel;

import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.logreader.LogSource;

import bibliothek.gui.dock.common.CControl;

/**
 * Eine Beschreibung einer spezifischen Log-File-Typs, wie z.B. ServiceResponseTime-Logs oder Error-Logs.
 * @author silvan.perego
 */
public interface FileTypeDescriptor {


	/**
	 * Erstelle die Extraction Fields, in der der Parser-Configuration, die für diesen Typ angemessen sind.
	 * @param ein JPanel mit den notwendigen ExtractionFields. Das JPanel implementiert das {@link JPanel} Interface.
	 * @return
	 */
	ExtractionFieldHandler createExtractionFieldPanel(ParserConfigDialog parserConfigDialog);
	
	/**
	 * Kreiere die JTabs, die für diesen FileType angemessen sind.
	 * @param logTracker
	 * @throws InterruptedException 
	 */
	void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?> logParser) throws InterruptedException;

	ConfiguredLogParser<?> createParser(String string);

	ConfiguredLogParser<?> convertLogParser(ConfiguredLogParser<?> configuredLogParser);

	void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?> logParser, Long obsStart);

	void removeDockables(CControl cControl);

	Object getControlDataConfig();

}

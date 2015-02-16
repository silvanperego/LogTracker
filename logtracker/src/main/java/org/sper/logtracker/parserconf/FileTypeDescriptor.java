package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.sper.logtracker.config.Configuration;

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
	void createAndRegisterTabs(JTabbedPane tabbedPane, Configuration configuration, ConfiguredLogParser logParser) throws InterruptedException;

	ConfiguredLogParser createParser(String string);

	ConfiguredLogParser convertLogParser(ConfiguredLogParser configuredLogParser);

	void setupDataPipeLines(List<String> fname, ConfiguredLogParser logParser, Long obsStart);

}

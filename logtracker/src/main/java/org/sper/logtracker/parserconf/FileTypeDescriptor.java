package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.JPanel;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.LogSource;

import bibliothek.gui.dock.common.CControl;

/**
 * Eine Beschreibung einer spezifischen Log-File-Typs, wie z.B. ServiceResponseTime-Logs oder Error-Logs.
 * @author silvan.perego
 */
public interface FileTypeDescriptor<T extends ConfiguredLogParser<T,R>,R> {


	/**
	 * Erstelle die Extraction Fields, in der der Parser-Configuration, die für diesen Typ angemessen sind.
	 * @param ein JPanel mit den notwendigen ExtractionFields. Das JPanel implementiert das {@link JPanel} Interface.
	 * @return
	 */
	ExtractionFieldHandler<T,R> createExtractionFieldPanel(ParserConfigPanel parserConfigDialog);
	
	/**
	 * Kreiere die JTabs, die für diesen FileType angemessen sind.
	 * @param logTracker
	 * @throws InterruptedException 
	 */
	void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser, GlobalConfig globalConfig) throws InterruptedException;

	T createParser(String string);

	T convertLogParser(ConfiguredLogParser<?,?> configuredLogParser);

	void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?,?> logParser, Long obsStart, ActivityMonitor activityMonitor, GlobalConfig globalConfig);

	void removeDockables(CControl cControl);

	Object getControlDataConfig();

	/**
	 * Wende Filetyp-spezifische Konfigurationen an.
	 * @param controlData
	 */
	void applyConfig(Object controlData);

}

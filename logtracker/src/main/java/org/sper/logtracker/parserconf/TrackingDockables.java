package org.sper.logtracker.parserconf;

import java.util.List;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.LogSource;

import bibliothek.gui.dock.common.CControl;

/**
 * Beschreibt die erstellten Dockables und mögliche Operationen darauf.
 * @author silvan.perego
 *
 */
public interface TrackingDockables {

	void removeDockables(CControl cControl);

	Object getControlDataConfig();

	/**
	 * Wende Filetyp-spezifische Konfigurationen an.
	 * @param controlData
	 */
	void applyConfig(Object controlData);

	void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?,?> logParser, Long obsStart, ActivityMonitor activityMonitor, GlobalConfig globalConfig);

}

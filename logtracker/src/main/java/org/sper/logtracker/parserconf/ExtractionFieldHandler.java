package org.sper.logtracker.parserconf;

import org.sper.logtracker.logreader.ConfiguredLogParser;

public interface ExtractionFieldHandler {

	void saveLoadedParser(ConfiguredLogParser loadedParser);

	void enableDetailFields(boolean b);

	void loadEditingFields(ConfiguredLogParser logParser);

	void removeErrorMarks();
	
	ConfiguredLogParser createParser(String parserName);
	
}
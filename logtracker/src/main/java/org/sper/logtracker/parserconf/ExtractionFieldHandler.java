package org.sper.logtracker.parserconf;


public interface ExtractionFieldHandler {

	void saveLoadedParser(ConfiguredLogParser<?> loadedParser);

	void enableDetailFields(boolean b);

	void loadEditingFields(ConfiguredLogParser<?> logParser);

	void removeErrorMarks();
	
	boolean verifyFormDataIsValid();
	
}
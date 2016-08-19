package org.sper.logtracker.parserconf;


public interface ExtractionFieldHandler<T extends ConfiguredLogParser<T, R>, R> {

	void saveLoadedParser(T loadedParser);

	void enableDetailFields(boolean b);

	void loadEditingFields(T logParser);

	void removeErrorMarks();
	
	boolean verifyFormDataIsValid();
	
}
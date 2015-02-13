package org.sper.logtracker.parserconf;

import org.sper.logtracker.logreader.ConfiguredLogParser;

public interface ExtractionFieldHandler {

	public abstract void saveLoadedParser(ConfiguredLogParser loadedParser);

	public abstract void enableDetailFields(boolean b);

	public abstract void loadEditingFields(ConfiguredLogParser logParser);

	public abstract void removeErrorMarks();

}
package org.sper.logtracker.config.compat;

import java.io.File;

public interface ConfigFileAction {

	void execConfigFileOperation(File selectedFile) throws Exception;

}

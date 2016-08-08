package org.sper.logtracker.config.compat;

import java.io.Serializable;

/**
 * Klassen, welche "Configuration Aware" sind, können bei Bedarf eine vorhandene Configuration intepretieren oder Teile davon erstellen.
 * @author silvan.perego
 */
public interface ConfigurationAware {

	/**
	 * Retourniere einen Schlüssel, welcher das Konfigurationsobjekt der Komponente eindeutig identifiziert.
	 * @return
	 */
	String getCompKey();
	/**
	 * Wende eine vorhandene Konfiguration an.
	 * @param cfg ein serialisierbares Konfigurationsobjekt.
	 */
	void applyConfig(Serializable cfg);
	
	/**
	 * Erstelle und retourniere ein Konfigurationsobjekt.
	 * @return das Konfigurationsobjekt.
	 */
	Serializable getConfig();
	
	/**
	 * @return true, falls dies ein dynamisches Modul ist, welches erst im Verlauf der Operationen nachgeladen wird.
	 */
	boolean isDynamicModule();
	
}

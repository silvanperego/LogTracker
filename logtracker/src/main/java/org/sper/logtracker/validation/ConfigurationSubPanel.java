package org.sper.logtracker.validation;

import javax.swing.*;

/**
 * Definiert alle Methode, die von einem Konfigurations-Subpanel erwartet werden.
 * @author silvan.perego
 */
public interface ConfigurationSubPanel {

	void setError(String txt);

	boolean verifyFormDataIsValid();

	void submit();

	JButton defaultButton();

}
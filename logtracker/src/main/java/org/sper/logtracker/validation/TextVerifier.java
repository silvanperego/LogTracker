package org.sper.logtracker.validation;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.sper.logtracker.parserconf.ParserConfigPanel;

/**
 * Verifiziert ein Textfeld. Zeigt die Meldung an und färbt das Feld Orange, falls ein Fehler aufgetreten ist.
 * @author silvan.perego
 *
 */
public abstract class TextVerifier extends InputVerifier {

	private ConfigurationSubPanel configDialog;
	
	public TextVerifier(ConfigurationSubPanel configDialog) {
		this.configDialog = configDialog;
	}

	/**
	 * Verifiziere den Inhalt eines Textfeldes.
	 * @param text der zu prüfende Inhalt.
	 * @return ein Fehlerbeschrieb, falls die Validierung fehlschlug, sonst null.
	 */
	protected abstract String verifyText(String text);
	
	@Override
	public final boolean verify(JComponent input) {
		JTextComponent textField = (JTextComponent) input;
		String error = verifyText(textField.getText());
		boolean result = error == null;
		configDialog.setError(error);
		input.setBackground(result ? ParserConfigPanel.getStandardBackgroundColor() : Color.ORANGE);
		return result;
	}

}

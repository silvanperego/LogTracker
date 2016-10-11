package org.sper.logtracker.validation;

import java.text.SimpleDateFormat;

public class SimpleDateFormatVerifier extends TextVerifier {
	public SimpleDateFormatVerifier(ConfigurationSubPanel configDialog) {
		super(configDialog);
	}

	@Override
	protected String verifyText(String text) {
		if (text != null && text.length() > 0) {
			try {
				new SimpleDateFormat(text);
			} catch (IllegalArgumentException e) {
				return "Not a valid SimpleDateFormat pattern";
			}
		} else {
			return "Date Format is mandatory";
		}
		return null;
	}
}
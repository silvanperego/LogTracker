package org.sper.logtracker.parserconf;

import javax.swing.*;

public class VerifyingPart {
	private JComponent component;
	private InputVerifier inputVerifier;

	public VerifyingPart(JComponent component, InputVerifier inputVerifier) {
		super();
		this.component = component;
		this.inputVerifier = inputVerifier;
	}
	
	boolean verify() {
		return inputVerifier.verify(component);
	}
}
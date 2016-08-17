package org.sper.logtracker.parserconf;

import javax.swing.DefaultComboBoxModel;

public class FieldIdxComboBoxModel extends DefaultComboBoxModel<Integer> {

	private static final long serialVersionUID = 1L;
	
	public FieldIdxComboBoxModel(int nfields, boolean withNull) {
		super();
		if (withNull)
			addElement(null);
		for (int i = 1; i <= nfields; i++)
			addElement(i);
	}

}

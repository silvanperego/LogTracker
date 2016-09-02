package org.sper.logtracker.util;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.location.AbstractTreeLocation;
import bibliothek.gui.dock.common.location.TreeLocationLeaf;


public class DockUtils {

	private static final double SPLIT_RATIO = 0.6;

	/**
	 * Errechnet die Lokation eines Dockables rechts des vorgegebenen. (Standard aside macht in der Regel "Stack").
	 * @param parentLocation
	 * @return
	 */
	public static CLocation aside(CLocation parentLocation) {
		if (parentLocation instanceof TreeLocationLeaf) {
			CLocation parent = parentLocation.getParent();
			return ((AbstractTreeLocation) parent).east(SPLIT_RATIO);
		} else if (parentLocation instanceof AbstractTreeLocation)
			return ((AbstractTreeLocation) parentLocation).east(0.5);
		return CLocation.base().normalEast(0.5);
	}

}

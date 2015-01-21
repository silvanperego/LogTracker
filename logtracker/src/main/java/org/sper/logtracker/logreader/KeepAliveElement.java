package org.sper.logtracker.logreader;

/**
 * Ein Element, welches aktive Komponenten (mit eigenem Thread) enthält, welche von aussen beendet werden können.
 * @author silvan.perego
 */
public interface KeepAliveElement {

	/**
	 * Markiert, dass dieser Log-Reader nach dem nächsten Zyklus beendet werden soll.
	 */
	public abstract void endOfLife();

}
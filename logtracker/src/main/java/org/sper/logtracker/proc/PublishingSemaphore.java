package org.sper.logtracker.proc;

/**
 * Der {@link PublishingSemaphore} zeigt an, ob die Publikation von Daten im Moment erfolgen kann oder nicht.
 * @author silvan.perego
 */
public interface PublishingSemaphore {

	boolean publish();
}

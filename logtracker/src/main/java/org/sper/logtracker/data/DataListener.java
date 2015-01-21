package org.sper.logtracker.data;


public interface DataListener<T> {

	/**
	 * Empfange eine Dateneinheit.
	 * @param df die Dateneinheit
	 */
	void receiveData(T data);
	
	/**
	 * Es ist ein günstiger Zeitpunkt eingetreten, um die Datenzu publizieren. (Normalerweise das momentane Ende eines Log-Files.)
	 */
	void publishData();
	
}

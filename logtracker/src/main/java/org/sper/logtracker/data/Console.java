package org.sper.logtracker.data;

public class Console {

	public interface MessageListener {

		public abstract void addMessage(String text);

	}
	
	private static MessageListener listener;

	public static void setListener(MessageListener listener) {
		Console.listener = listener;
	}
	
	public static void addMessage(String text) {
		listener.addMessage(text);
	}

}

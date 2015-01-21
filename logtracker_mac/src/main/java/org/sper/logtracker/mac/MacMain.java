package org.sper.logtracker.mac;

import java.io.File;

import org.sper.logtracker.ui.LogTracker;

import com.github.axet.desktop.os.mac.AppleHandlers;

/**
 * Mac OS Application-Package Wrapper. Dies ist notwendig, damit 
 * der Log-Tracker durch Doppelclick eines Files geöffnet werden kann.
 * @author silvan.perego
 */
public class MacMain {

	public static void main(String[] args) throws Exception {
		AppleHandlers.getAppleHandlers().addOpenFileListener(new AppleHandlers.OpenFileHandler() {
			@Override
			public void openFile(File f) {
				LogTracker.setCfgFile(f.getPath());
			}
		});
		LogTracker.main(args);
	}

}

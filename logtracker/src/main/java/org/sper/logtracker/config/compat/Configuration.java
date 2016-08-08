package org.sper.logtracker.config.compat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Repräsentiert eine speicherbare LogTracker-Konfiguration.
 * @author silvan.perego
 *
 */
public class Configuration {

	private Map<String, ConfigurationAware> configModule = new HashMap<String, ConfigurationAware>();
	private Map<String, Serializable> activeConfig;
	
	public void registerModule(ConfigurationAware module) {
		configModule.put(module.getCompKey(), module);
		if (activeConfig != null) {
			for (Entry<String, Serializable> entry : activeConfig.entrySet()) {
				if (module.getCompKey().equals(entry.getKey())) {
					module.applyConfig(entry.getValue());
				}
			}
		}
	}
	
	public void unregisterModule(ConfigurationAware module) {
		configModule.remove(module.getCompKey());
	}
	
	@SuppressWarnings("unchecked")
	public void loadConfiguration(File f) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			activeConfig = (Map<String, Serializable>) ois.readObject();
			for (Entry<String, Serializable> entry : activeConfig.entrySet()) {
				ConfigurationAware configurationAware = configModule.get(entry.getKey());
				if (configurationAware != null) {
					configurationAware.applyConfig(entry.getValue());
				}
			}
		} finally {
			if (ois != null)
				ois.close();
		}
	}

	public void resetDynamicModules() {
		for (Iterator<Entry<String, ConfigurationAware>> it = configModule.entrySet().iterator(); it.hasNext(); ) {
			if (it.next().getValue().isDynamicModule())
				it.remove();
		}
	}
	
	public void resetActiveConfig() {
		activeConfig = null;
	}
}

package org.sper.logtracker.config.compat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	public void safeToFile(File f) throws IOException {
		Map<String, Serializable> configMap = new HashMap<String, Serializable>();
		for (ConfigurationAware module : configModule.values()) {
			Serializable modconf = module.getConfig();
			if (modconf != null)
				configMap.put(module.getCompKey(), modconf);
		}
		ObjectOutputStream oos = null;
		try {
			String suffix = ".ltc";
			if (!f.getAbsolutePath().endsWith(suffix))
				f = new File(f + suffix);
			FileOutputStream fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(configMap);
		} finally {
			if (oos != null)
				oos.close();
		}
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

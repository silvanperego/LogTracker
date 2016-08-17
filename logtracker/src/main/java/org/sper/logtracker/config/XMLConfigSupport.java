package org.sper.logtracker.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Realisiert die neue Konfigurationsmethode mit XML-Files.
 * @author silvan.perego
 */
public class XMLConfigSupport {

	public void saveXMLConfig(File selectedFile, LogTrackerConfig config) {
		try {
			String suffix = ".ltc";
			if (!selectedFile.getAbsolutePath().endsWith(suffix))
				selectedFile = new File(selectedFile + suffix);
			JAXBContext jaxbContext = JAXBContext.newInstance(LogTrackerConfig.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(config, selectedFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public LogTrackerConfig loadConfiguration(File selectedFile) throws Exception {
		try (Reader fr = new BufferedReader(new FileReader(selectedFile))) {
			String magic = "<?xml";
			char[] header = new char[magic.length()];
			fr.mark(magic.length());
			fr.read(header);
			if (magic.equals(new String(header))) {
				JAXBContext jaxbContext = JAXBContext.newInstance(LogTrackerConfig.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				fr.reset();
				LogTrackerConfig logTrackerconfig = (LogTrackerConfig) unmarshaller.unmarshal(fr);
				return logTrackerconfig;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	
	
}

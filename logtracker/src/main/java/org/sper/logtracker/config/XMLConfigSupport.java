package org.sper.logtracker.config;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

	
	
}

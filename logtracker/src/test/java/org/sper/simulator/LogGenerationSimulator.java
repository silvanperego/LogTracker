package org.sper.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LogGenerationSimulator {

	/**
	 * Liest ein bestehendes Log-File und kopiert es in Tranchen an eine neue Position.
	 * Dies dient der Simulation eines kontinuierlich entstehenden Log-Files und zum Testen
	 * des KeepAlive-LogReaders.
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 3) {
			System.err.println("Usage: LogGenerationSimulator <src-file> <dst-File> <chunk-size>");
			System.exit(1);
		}
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		FileWriter writer = new FileWriter(args[1]);
		int chunkSize = Integer.parseInt(args[2]);
		int lineno = 0;
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			writer.write(line);
			writer.write('\n');
			if (!line.startsWith("#") && ++lineno % chunkSize == 0)
				Thread.sleep(10000);
			writer.flush();
		}
		reader.close();
		writer.close();
	}

}

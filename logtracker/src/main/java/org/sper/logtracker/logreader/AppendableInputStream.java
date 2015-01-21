package org.sper.logtracker.logreader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Erlaubt den on-the-fly Austausch eines darunterliegenden InputStreams.
 * @author silvan.perego
 *
 */
public class AppendableInputStream extends InputStream {

	private InputStream source;
	
	@Override
	public int read() throws IOException {
		return source.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return source.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return source.read(b);
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

	public void setSource(InputStream source) {
		this.source = source;
	}

}

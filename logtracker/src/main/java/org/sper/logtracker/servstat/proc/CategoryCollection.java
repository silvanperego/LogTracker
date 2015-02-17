package org.sper.logtracker.servstat.proc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Erlaubt die eindeutige Bestimmung einer Sammlung von Service-Indices.
 * @author sper
 */
public class CategoryCollection implements Iterable<Integer> {
	
	private long[] key = new long[1];
	
	public CategoryCollection() {
	}
	
	public void addFactoryCat(int idx) {
		int pos = idx / 64;
		int offs = idx % 64;
		if (pos >= key.length) {
			key = Arrays.copyOf(key, pos + 1);
		}
		key[pos] |= 1L << offs;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int idx = 0;
			long itKey = key[0];
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Integer next() {
				if (!hasNext())
					throw new NoSuchElementException();
				while ((itKey & 1) == 0) {
					itKey >>= 1;
					idx++;
					if (idx % 64 == 0) {
						itKey = key[idx / 64];
					}
				}
				itKey >>= 1;
				return idx++;
			}
			
			@Override
			public boolean hasNext() {
				return itKey > 0 || (idx + 1) / 64 + 1 < key.length;
			}
		};
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < key.length; i++)
			hash = 31 * hash + (int)(key[i] ^ (key[i] >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != CategoryCollection.class)
			return false;
		CategoryCollection other = (CategoryCollection) obj;
		if (key.length != other.key.length)
			return false;
		for (int i = 0; i < key.length; i++)
			if (key[i] != other.key[i])
				return false;
		return true;
	}
	
	public boolean contains(int idx) {
		int pos = idx / 64;
		int offs = idx % 64;
		return (key[pos] & (1L << offs)) > 0;
	}

}

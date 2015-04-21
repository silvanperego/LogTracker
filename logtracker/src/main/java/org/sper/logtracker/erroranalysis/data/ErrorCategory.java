package org.sper.logtracker.erroranalysis.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Beschreibt eine Meldungsklasse, welche dynamisch ermittelt wurde. Die Meldungsklasse besteht aus typischen Key-Words
 * und einer Liste aller Raw-Meldungen, welche dieser Klasse zugeordnet wurden.
 * @author silvan.perego
 */
public class ErrorCategory implements Iterable<RawErrorDataPoint> {

	/**
	 * Die Schlüsselwörter, die diese Klasse repräsentieren.
	 */
	private Set<String> keyWordSet = new HashSet<String>();
	/**
	 * Die Anzahl Wörter, die in Meldungen dieser Klasse normalerweise gefunden werden.
	 */
	private int totalWords;

	/**
	 * Eine Liste mit allen Meldungen, welche dieser Kategorie zugeordnet wurden.
	 */
	private List<RawErrorDataPoint> errorList = new ArrayList<RawErrorDataPoint>();
	
	private static final Pattern wordSep = Pattern.compile("[^\\w/]");
	private HashSet<String> intersectSet;
	private String[] split;
	private RawErrorDataPoint lastPoint;
	private String severity;

	public ErrorCategory(RawErrorDataPoint dp) {
		split = wordSep.split(dp.msg);
		totalWords = split.length;
		errorList.add(dp);
		keyWordSet = new HashSet<String>();
		severity = dp.severity;
		for (String word : split) {
			keyWordSet.add(word);
		}
	}

	/**
	 * Berechnet einen Score, welcher angibt, wie gut eine neue Meldung zu dieser Kategorie passt.
	 * Behält Analyseergebnisse für eine eventuelle spätere Speicherung im Speicher. 
	 * @param dp der Datenpunkt
	 * @return der berechnete Score.
	 */
	int score(RawErrorDataPoint dp) {
		if (!(severity == dp.severity || severity != null && severity.equals(dp.severity)))
			return 0;
		lastPoint = dp;
		split = wordSep.split(dp.msg);
		if (split.length < 2)
			return 0;
		intersectSet = new HashSet<String>(keyWordSet);
		intersectSet.retainAll(Arrays.asList(split));
		int meanWords = totalWords / errorList.size();
		int score = 100 * intersectSet.size() /  meanWords * (meanWords - Math.abs(split.length - meanWords)) / meanWords;
		return score;
	}
	
	/**
	 * Der letzte Datenpunkt, welche mittels {@link #score(RawErrorDataPoint)} analysiert wurde, wird definitiv
	 * dieser Kategorie zugeschlagen.
	 */
	void addDataPoint() {
		totalWords += split.length;
		errorList.add(lastPoint);
		keyWordSet = intersectSet;
	}
	
	public RawErrorDataPoint getLatestMessage() {
		return errorList.get(errorList.size() - 1);
	}

	public int getNumMessages() {
		return errorList.size();
	}

	@Override
	public Iterator<RawErrorDataPoint> iterator() {
		return errorList.iterator();
	}
}

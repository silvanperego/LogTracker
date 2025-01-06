package org.sper.logtracker.erroranalysis.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Beschreibt eine Meldungsklasse, welche dynamisch ermittelt wurde. Die Meldungsklasse besteht aus typischen Key-Words
 * und einer Liste aller Raw-Meldungen, welche dieser Klasse zugeordnet wurden.
 * @author silvan.perego
 */
public class ErrorCategory implements Comparable<ErrorCategory> {

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
	
	private static final Pattern wordSep = Pattern.compile("[^\\w/\\.:-]+");
	private static final Color LIGHT_GREEN = new Color(0xa0ffa0);
	private static final Color LIGHT_RED = new Color(0xffa0a0);
	private HashSet<String> intersectSet;
	private String[] split;
	private RawErrorDataPoint lastPoint;
	private String severity;
	private int relevance;
	private Color relevanceColor;

	public ErrorCategory(RawErrorDataPoint dp) throws NoContextException {
		split = splitLine(dp);
		totalWords = split.length;
		if (totalWords == 0) {
			throw new NoContextException();
		}
		errorList.add(dp);
		keyWordSet = new HashSet<String>();
		severity = dp.severity;
		if ("CRITICAL".equals(severity)) {
			relevance = 3000;
			relevanceColor = Color.MAGENTA;
		} else if ("ERROR".equals(severity)) {
			relevance = 2000;
			relevanceColor = LIGHT_RED;
		} else if ("WARNING".equals(severity)) {
			relevance = 1000;
			relevanceColor = Color.YELLOW;
		} else 
			relevanceColor = LIGHT_GREEN;
		for (String word : split) {
			keyWordSet.add(word);
		}
	}

	/**
	 * Trenne die Zeile in separate Wörter. Berücksichtige bei sehr langen Zeilen nicht alles.
	 * @param dp der DataPoint.
	 * @return
	 */
	private String[] splitLine(RawErrorDataPoint dp) {
		String[] split = wordSep.split(dp.msg);
		if (split.length > 20)
			split = Arrays.copyOfRange(split, 0, 20);
		return split;
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
		split = splitLine(dp);
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
	synchronized void addDataPoint() {
		totalWords += split.length;
		errorList.add(lastPoint);
		keyWordSet = intersectSet;
	}
	
	public synchronized RawErrorDataPoint getLatestMessage() {
		return errorList.get(errorList.size() - 1);
	}

	public synchronized int getNumMessages() {
		return errorList.size();
	}

	public List<RawErrorDataPoint> getErrorsAsList() {
		return errorList;
	}

	/**
	 * Vergleicht die Kategorien bezüglich Relevanz.
	 */
	@Override
	public int compareTo(ErrorCategory o) {
		if (relevance != o.relevance)
			return (relevance < o.relevance ? 1 : 0) - (relevance > o.relevance ? 1 : 0);
		RawErrorDataPoint latestMessage = getLatestMessage();
		RawErrorDataPoint otherMessage = o.getLatestMessage();
		if (latestMessage.occTime != null && otherMessage.occTime != null)
			return otherMessage.occTime.compareTo(latestMessage.occTime);
		return 0;
	}
	
	public Color getRelevanceColor() {
		return ((System.currentTimeMillis() - getLatestMessage().occTime.longValue()) < 600000L) ? relevanceColor : null;
	}

}

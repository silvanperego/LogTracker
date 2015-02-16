package org.sper.logtracker.parserconf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserList extends ArrayList<ConfiguredLogParser> {

	private static final long serialVersionUID = 1L;
	private static final Pattern NUM_END_PAT = Pattern.compile(".* \\((\\d+)\\)");

	private void addParserConfig(ConfiguredLogParser logParser) {
		// Falls der gewünschte Name bereits vergeben ist, füge eine Laufnummer in Klammern hinzu.
		int extNum = 0;
		String name = logParser.getName();
		Matcher m = NUM_END_PAT.matcher(name);
		if (m.matches())
			extNum = Integer.parseInt(m.group(1));
		until_unused_name:
			while (true) {
				for (ConfiguredLogParser parser : this) {
					if (name.equals(parser.getName())) {
						name = new StringBuilder().append(logParser.getName()).append(" (").append(++extNum).append(")").toString();
						continue until_unused_name;
					}
				}
				break;
			}
		// Jetzt Parser mit diesem Namen hinzufügen.
		logParser.setName(name);
		if (isEmpty() || get(size() - 1).getName() != null)
			add(logParser);
		else
			add(size() - 1, logParser);
	}
	
	public void addParserConfigs(List<ConfiguredLogParser> logParserList) {
		for (ConfiguredLogParser logParser : logParserList) {
			addParserConfig(logParser);
		}
	}
}

package org.sper.logtracker.parserconf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserConfigList extends ArrayList<ConfiguredLogParser<?>> {

	private static final long serialVersionUID = 1L;
	private static final Pattern NUM_END_PAT = Pattern.compile(".* \\((\\d+)\\)");
	private List<ChangeListener> listenerList = new ArrayList<>();
	
	public interface ChangeListener {
		void modelChanged();
	}
	
	public void addChangeListener(ChangeListener listener) {
		listenerList.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public boolean add(ConfiguredLogParser<?> paramE) {
		final boolean result = super.add(checkNameUnqiue(paramE));
		return result;
	}

	public void markModelChanged() {
		listenerList.forEach(ChangeListener::modelChanged);
	}

	@Override
	public void add(int paramInt, ConfiguredLogParser<?> paramE) {
		super.add(paramInt, checkNameUnqiue(paramE));
	}

	@Override
	public boolean addAll(Collection<? extends ConfiguredLogParser<?>> paramCollection) {
		for (ConfiguredLogParser<?> parser : paramCollection) {
			checkNameUnqiue(parser);
		}
		final boolean result = super.addAll(paramCollection);
		return result;
	}

	private ConfiguredLogParser<?> checkNameUnqiue(ConfiguredLogParser<?> logParser) {
		// Falls der gewünschte Name bereits vergeben ist, füge eine Laufnummer in Klammern hinzu.
		int extNum = 0;
		String name = logParser.getName();
		Matcher m = NUM_END_PAT.matcher(name);
		if (m.matches())
			extNum = Integer.parseInt(m.group(1));
		until_unused_name:
			while (true) {
				for (ConfiguredLogParser<?> parser : this) {
					if (name.equals(parser.getName())) {
						name = new StringBuilder().append(logParser.getName()).append(" (").append(++extNum).append(")").toString();
						continue until_unused_name;
					}
				}
				break;
			}
		// Jetzt Parser mit diesem Namen hinzufügen.
		logParser.setName(name);
		return logParser;
	}

}

package org.sper.logtracker.parserconf;

import java.util.List;

public interface DefaultParserProvider {

	List<ConfiguredLogParser> getDefaultLogParsers();

}
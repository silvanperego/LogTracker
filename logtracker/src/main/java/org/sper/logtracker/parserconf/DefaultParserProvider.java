package org.sper.logtracker.parserconf;

import java.util.List;

import org.sper.logtracker.logreader.ConfiguredLogParser;

public interface DefaultParserProvider {

	List<ConfiguredLogParser> getDefaultLogParsers();

}
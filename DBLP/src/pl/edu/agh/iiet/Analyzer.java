package pl.edu.agh.iiet;

import org.xml.sax.SAXException;
import pl.edu.agh.iiet.model.Dblp;
import pl.edu.agh.ztis.jcr.JcrCrawlerRunner;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Analyzer {
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		if (args.length < 2) {
			usage();
		}
		Map<String, JCREntry> jcrEntryMap = null;
		if (args.length > 2) {
			jcrEntryMap = fetchJournalToImpactFactorMap();
		}
		Dblp dblp = Parser.getDblpGraphFromFile(new File(args[0]), jcrEntryMap);
		dblp.printStatisticsAndInitializeDB(args[1]);
	}

	private static Map<String, JCREntry> fetchJournalToImpactFactorMap() {
		JcrCrawlerRunner runner = JcrCrawlerRunner.getInstance();
		List<JCREntry> run = runner.run(10);
		return run.stream().collect(Collectors.toMap(JCREntry::getTitle, Function.identity()));
	}

	private static void usage() {
		System.err.println("Usage: run.sh [input file name] [csv.file.name] [fetchJcr]");
		System.exit(-1);
	}
}

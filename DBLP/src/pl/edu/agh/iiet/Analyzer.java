package pl.edu.agh.iiet;

import org.xml.sax.SAXException;
import pl.edu.agh.iet.MinistryListParser;
import pl.edu.agh.iet.model.MinistryListEntry;
import pl.edu.agh.iiet.matcher.JcrMinistryListMatcher;
import pl.edu.agh.iiet.model.Dblp;
import pl.edu.agh.iiet.model.MinistryListEntryJCREntryPair;
import pl.edu.agh.ztis.jcr.JcrCrawlerRunner;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Analyzer {
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		if (args.length < 2) {
			usage();
		}
		List<MinistryListEntryJCREntryPair> jcrAndMinistryData = null;
		if (args.length > 2) {
			System.out.println("Fetching JCR and Ministry Data.");
			jcrAndMinistryData = fetchJcrAndMinistryData();
			System.out.println("Done fetching JCR and Ministry Data.");
		}

		Dblp dblp = Parser.getDblpGraphFromFile(new File(args[0]), jcrAndMinistryData);
		dblp.printStatisticsAndInitializeDB(args[1]);
	}

	private static List<MinistryListEntryJCREntryPair> fetchJcrAndMinistryData() throws IOException {
		JcrCrawlerRunner runner = JcrCrawlerRunner.getInstance();
		List<JCREntry> run = runner.run();
		List<MinistryListEntry> ministryListEntries = new MinistryListParser().parse("ministry_list.txt");
		return new JcrMinistryListMatcher().match(run, ministryListEntries);
	}

	private static void usage() {
		System.err.println("Usage: run.sh [input file name] [csv.file.name] [fetchJcr]");
		System.exit(-1);
	}
}

package pl.edu.agh.iiet;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import pl.edu.agh.iiet.model.Dblp;

public class Analyzer {
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		if (args.length != 2) {
			usage();
		}
		Dblp dblp = Parser.getDblpGraphFromFile(new File(args[0]));
		dblp.printStatisticsAndInitializeDB(args[1]);
	}

	private static void usage() {
		System.err.println("Usage: run.sh [input file name] [csv.file.name]");
		System.exit(-1);
	}
}

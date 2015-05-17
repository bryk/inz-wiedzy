package pl.edu.agh.iiet.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.common.collect.ImmutableList;

/**
 * Main DBLP graph object containing all authors and all publications.
 */
public final class Dblp {
	private final ImmutableList<Author> authors;
	private final ImmutableList<Publication> publications;

	public Dblp(ImmutableList<Author> authors,
			ImmutableList<Publication> publications,
			Map<String, Author> authorsByName) {
		this.authors = authors;
		this.publications = publications;
	}

	public ImmutableList<Author> getAuthors() {
		return authors;
	}

	public ImmutableList<Publication> getPublications() {
		return publications;
	}

	public void debugPrint() {
		authors.stream().sorted((a, b) -> a.getName().compareTo(b.getName()))
				.forEach(e -> System.out.println(e));
		publications.stream().forEach(e -> System.out.println(e));
	}

	/**
	 * Prints author and publication networks to CSV files to be used in GEPHI.
	 */
	private void printForCfinder(String file) throws IOException {
		CSVPrinter authoredgescsv = new CSVPrinter(new FileWriter(new File(file
				+ "-cfinder.csv")), CSVFormat.TDF);

		Map<String, Map<String, Integer>> data = new HashMap<>();
		
		int k = 0 ;
		for (int i = 0; i < authors.size(); i++) {
			Author a = authors.get(i);
			if (!data.containsKey(a.getName())) {
				data.put(a.getName(), new HashMap<>());
			}
			for (Publication pub : a.getPublications()) {
				if (pub.shouldBeAddedToSeries()) {
					for (Author other : pub.getAuthors()) {
						if (!data.containsKey(other.getName())) {
							if (k % 1000 == 0) {
								System.out.println("Mapped author number " + i);
							}
							k++;
							Map<String, Integer> otherData = data.get(a
									.getName());
							if (!otherData.containsKey(other.getName())) {
								otherData.put(other.getName(), 0);
							}
							otherData.put(other.getName(),
									otherData.get(other.getName()) + 1);
						}
					}
				}
			}
		}
		for (Entry<String, Map<String, Integer>> entry : data.entrySet()) {
			for (Entry<String, Integer> child : entry.getValue().entrySet()) {
				authoredgescsv.printRecord(
						entry.getKey().replaceAll("\\s+", "_"), child.getKey()
								.replaceAll("\\s+", "_"), child.getValue());
			}
		}
		authoredgescsv.close();
	}

	/**
	 * Prints author and publication networks to CSV files to be used in GEPHI.
	 */
	private void printGraphsToCsvFile(String file) throws IOException {
		CSVPrinter csv = new CSVPrinter(new FileWriter(new File(file
				+ "-pubnodes.csv")), CSVFormat.EXCEL);
		Publication.printCsvHeader(csv);
		for (int i = 0; i < publications.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println("CSVed publication number " + i);
			}
			if (publications.get(i).shouldBeAddedToSeries())
				publications.get(i).toCsvWithoutAuthors(csv);
		}
		csv = new CSVPrinter(new FileWriter(new File(file + "-pubedges.csv")),
				CSVFormat.EXCEL);
		CSVPrinter authornodescsv = new CSVPrinter(new FileWriter(new File(file
				+ "-authornodes.csv")), CSVFormat.EXCEL);
		authornodescsv.printRecord("Id", "Label");
		CSVPrinter authoredgescsv = new CSVPrinter(new FileWriter(new File(file
				+ "-authoredges.csv")), CSVFormat.EXCEL);
		authoredgescsv.printRecord("Source", "Target");
		csv.printRecord("Source", "Target");
		for (int i = 0; i < authors.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println("CSVed author number " + i);
			}
			authors.get(i).toPublicationEdges(csv);
			authors.get(i).toCsvNode(authornodescsv);
			authors.get(i).toEdgeCsv(authoredgescsv);
		}
		csv.close();
	}

	private void printGraphPerYear(String filename) throws IOException {
		ArrayList<Author> copyOfAuthors = new ArrayList<Author>();
		for (Author author : authors) {
			copyOfAuthors.add(author);
		}
		for (int year = 1966; year < 2014; year = year + 1) {
			printGraphToCsvFilePerYear(filename, year, year + 1, copyOfAuthors);
		}
	}

	private void printGraphToCsvFilePerYear(String file, int begin, int end,
			ArrayList<Author> authorList) throws IOException {
		CSVPrinter authornodescsv = new CSVPrinter(new FileWriter(new File(file
				+ "-" + begin + "-" + end + "authornodes.csv")),
				CSVFormat.EXCEL);
		authornodescsv.printRecord("Id", "Label");
		CSVPrinter authoredgescsv = new CSVPrinter(new FileWriter(new File(file
				+ "-" + begin + "-" + end + "authoredges.csv")),
				CSVFormat.EXCEL);
		authoredgescsv.printRecord("Source", "Target");
		Collections.sort(authorList, new Comparator<Author>() {
			public int compare(Author a1, Author a2) {
				int c1 = 0;
				for (Publication p : a1.getPublications())
					if (begin <= p.getYear() && end > p.getYear())
						c1++;
				int c2 = 0;
				for (Publication p : a2.getPublications())
					if (begin <= p.getYear() && end > p.getYear())
						c2++;
				a1.count = c1;
				a2.count = c2;
				return c2 - c1;
			}
		});
		HashSet<Integer> ids = new HashSet<Integer>();
		int threshold = 50000;
		for (int i = 0; i < threshold; i++) {
			ids.add(authorList.get(i).getId());
		}
		System.out.println("Year: " + begin + " Count: "
				+ authorList.get(0).count + " Name: "
				+ authorList.get(0).getName());
		for (int i = 0; i < threshold; i++) {
			Author author = authorList.get(i);

			author.toCsvNode(authornodescsv);
			author.toEdgeCsv(authoredgescsv, ids, begin, end);
		}
		authornodescsv.flush();
		authornodescsv.close();

		authoredgescsv.flush();
		authoredgescsv.close();
	}

	public void printStatisticsAndInitializeDB(String file) throws IOException {
		System.out.printf("Number of publications: %d\n", publications.size());
		int longestAuthor = authors.stream().map(a -> a.getName().length())
				.reduce((a, b) -> Math.max(a, b)).get();
		System.out.println("Longest author name:" + longestAuthor);
		int longestPub = publications.stream()
				.map(a -> a.getName() != null ? a.getName().length() : 0)
				.reduce((a, b) -> Math.max(a, b)).get();
		System.out.println("Longest pub name:" + longestPub);
		int longestPublisher = publications
				.stream()
				.map(a -> a.getPublisher() != null ? a.getPublisher().length()
						: 0).reduce((a, b) -> Math.max(a, b)).get();
		System.out.println("Longest publisher:" + longestPublisher);
		publications
				.stream()
				.collect(Collectors.groupingBy(Publication::getType))
				.forEach(
						(type, vals) -> System.out
								.printf("Publication type: %s, number of publications: %d\n",
										type, vals.size()));

		System.out.printf("Number of authors of those publications: %d\n",
				authors.size());

		// debugPrint();
		printGraphsToCsvFile(file);
		//printForCfinder(file);
	}
}

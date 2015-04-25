package pl.edu.agh.iiet.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.common.collect.ImmutableList;

/**
 * Main DBLP graph object containing all authors and all publications.
 */
public final class Dblp {
	private final ImmutableList<Author> authors;
	private final ImmutableList<Publication> publications;
	private final Map<String, Author> authorsByName;

	public Dblp(ImmutableList<Author> authors,
			ImmutableList<Publication> publications,
			Map<String, Author> authorsByName) {
		this.authors = authors;
		this.publications = publications;
		this.authorsByName = authorsByName;
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
	public void printGraphsToCsvFile(String file) throws IOException {
		CSVPrinter csv = new CSVPrinter(new FileWriter(new File(file
				+ "-pubnodes.csv")), CSVFormat.EXCEL);
		csv.printRecord("Id", "Label", "publicationYear",
				"publicationPublisher", "publicationType", "publicationKey",
				"publicationJournal");
		for (int i = 0; i < publications.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println("CSVed publication " + i);
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
				System.out.println("CSVed author " + i);
			}
			authors.get(i).toPublicationEdges(csv);
			authors.get(i).toCsvNode(authornodescsv);
			authors.get(i).toEdgeCsv(authoredgescsv);
		}
	}

	public void printGraphPerYear(String filename) throws IOException {
		ArrayList<Author> copyOfAuthors = new ArrayList<Author>();
		for (Author author : authors) {
			copyOfAuthors.add(author);
		}
		for (int year = 1966; year < 2014; year = year + 1) {
			// System.out.println("CSV save years: " + year + " " + (year + 1));
			printGraphToCsvFilePerYear(filename, year, year + 1, copyOfAuthors);
		}
	}

	public void printGraphToCsvFilePerYear(String file, int begin, int end,
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
		int threshold = 500;
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
		// printGraphsToCsvFile(file);
		// printGraphPerYear(file);
		//debugPrint();

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

		printGraphsToCsvFile("dblp-out.csv");
		
		// initDB();
	}

	public void initDB() {
		String drop_table_author = "DROP TABLE IF EXISTS AUTHOR CASCADE; ";
		String create_table_author = "CREATE TABLE AUTHOR "
				+ "(id INTEGER not NULL, " + " name VARCHAR(58), "
				+ " PRIMARY KEY ( id ))";
		String drop_table_publ = "DROP TABLE IF EXISTS PUBLICATION CASCADE; ";
		String create_table_publ = "CREATE TABLE PUBLICATION "
				+ "(id INTEGER not NULL, " + " title VARCHAR(511), "
				+ " year INTEGER, " + " publisher VARCHAR(162), "
				+ " type VARCHAR(20)," + " PRIMARY KEY ( id ))";

		String drop_table_publ_a = "DROP TABLE IF EXISTS PUBLICATIONS_AUTHOR CASCADE; ";

		String create_table_publ_a = "CREATE TABLE PUBLICATIONS_AUTHOR "
				+ "(author_id INT, " + "publ_id INT, "
				+ "FOREIGN KEY (author_id) " + "REFERENCES AUTHOR(id) "
				+ "ON DELETE CASCADE, " + "FOREIGN KEY (publ_id) "
				+ "REFERENCES PUBLICATION(id) " + "ON DELETE CASCADE ) ";
		execStatement(drop_table_publ_a);
		execStatement(drop_table_author);
		execStatement(drop_table_publ);
		execStatement(create_table_author);
		execStatement(create_table_publ);
		execStatement(create_table_publ_a);

		LinkedList<String> insertAuthors = new LinkedList<String>();
		for (Author author : authors) {
			insertAuthors.add("INSERT INTO AUTHOR VALUES ( "
					+ Integer.valueOf(author.getId())
					+ ", \""
					+ (author.getName() != null ? author.getName().replace(
							"\"", "") : null) + "\" )");
		}
		execStatements(insertAuthors);

		LinkedList<String> insertPublications = new LinkedList<String>();
		for (Publication publication : publications) {
			insertPublications.add("INSERT INTO PUBLICATION VALUES ( "
					+ Integer.valueOf(publication.getId())
					+ ", \""
					+ (publication.getName() != null ? publication.getName()
							.replace("\"", "") : null) + "\", "
					+ Integer.valueOf(publication.getYear()) + ", \""
					+ publication.getPublisher() + "\", \""
					+ publication.getType().toString() + "\" )");
		}
		execStatements(insertPublications);

		LinkedList<String> insertPublicationsAuthors = new LinkedList<String>();
		for (Publication publication : publications) {
			for (Author author : publication.getAuthors()) {
				insertPublicationsAuthors
						.add("INSERT INTO PUBLICATIONS_AUTHOR VALUES ( "
								+ Integer.valueOf(author.getId()) + ", "
								+ Integer.valueOf(publication.getId()) + ")");
			}
		}
		execStatements(insertPublicationsAuthors);
	}

	public void execStatement(String stmt) {
		LinkedList<String> list = new LinkedList<String>();
		list.add(stmt);
		execStatements(list);
	}

	public void execStatements(LinkedList<String> stmts) {
		String dbUrl = "jdbc:mysql://localhost/mydatabase";
		String dbClass = "com.mysql.jdbc.Driver";

		String username = "root";
		String password = "";

		try {

			Class.forName(dbClass);
			Connection connection = DriverManager.getConnection(dbUrl,
					username, password);
			Statement statement = connection.createStatement();
			for (int i = 0; i < stmts.size(); i++) {
				if (i % 100 == 0)
					System.out.println("Exec: " + stmts.get(i));
				statement.executeUpdate(stmts.get(i));
			}
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

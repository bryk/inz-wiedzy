package pl.edu.agh.iiet.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


import org.apache.commons.csv.CSVPrinter;

public final class Author {
	private static int totalNumberOfAuthors = 0;

	private final int id;
	private final String name;
	private final List<Publication> publications;
    public int count = 0;
	public static final class Builder {
		private String name;

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Author build() {
			return new Author(name, new ArrayList<Publication>());
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	Author(String name, List<Publication> publications) {
		this.id = totalNumberOfAuthors++;
		this.name = name.replaceAll("\\s+", " ").trim();
		this.publications = publications;
	}

	public final String getName() {
		return name;
	}

	public final int getId() {
		return this.id;
	}

	public final void addPublication(Publication publication) {
		this.publications.add(publication);
	}

	public List<Publication> getPublications() {
		return publications;
	}

	@Override
	public String toString() {
		return "Author [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		return id * 991;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void toCsvNode(CSVPrinter csv) throws IOException {
		csv.printRecord(getId(), getName());
	}

	public void toEdgeCsv(CSVPrinter csv) throws IOException {
		for (Publication publication : publications) {
			if (publication.shouldBeAddedToSeries()) {
				for (Author author : publication.getAuthors()) {
					csv.printRecord(this.getId(), author.getId());
				}
			}
		}
	}

    public void toEdgeCsv(CSVPrinter csv, HashSet<Integer> ids, int begin, int end) throws IOException {
		for (Publication publication : publications) {
			if (publication.getYear() >= begin && publication.getYear() < end) {
				for (Author author : publication.getAuthors()) {
                    if(ids.contains(author.getId()))
					    csv.printRecord(this.getId(), author.getId());
				}
			}
		}
	}



	public void toPublicationEdges(CSVPrinter csv) throws IOException {
		for (Publication pub1 : publications) {
			for (Publication pub2 : publications) {
				if (pub1 != pub2 && pub1.shouldBeAddedToSeries() && pub2.shouldBeAddedToSeries()) {
					csv.printRecord(pub1.getId(), pub2.getId());
					csv.printRecord(pub2.getId(), pub1.getId());
				}
			}
		}
	}
}

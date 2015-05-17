package pl.edu.agh.iiet.model;

import com.google.common.collect.ImmutableList;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Publication {
	private static int totalNumberOfAuthors = 0;

    private final int id;
    private final String key;
    private final String title;
    private final int year;
    private final String publisher;
    private final String journal;
    private final String journalKey;
    private final ImmutableList<Author> authors;
    private final PublicationType type;
    private final String crossref;
    private final Integer numPages;
    private final Double journalImpactFactor;


    Publication(String title, int year, String publisherName,
                ImmutableList<Author> authors, PublicationType publicationType,
                String key, String journalKey, String crossref, String journal,
                Integer numPages, Double journalImpactFactor) {
        this.id = totalNumberOfAuthors++;
        this.title = title;
        this.year = year;
        this.publisher = publisherName;
        this.authors = authors;
        this.type = publicationType;
        this.key = key;
        this.journalKey = journalKey;
        this.crossref = crossref;
        this.journal = journal;
        this.numPages = numPages;
        this.journalImpactFactor = journalImpactFactor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void printCsvHeader(CSVPrinter csv) throws IOException {
        csv.printRecord("Id", "Name", "publicationYear", "publicationType",
                "publicationKeyFull", "journalKey", "publicationJournal",
                "numPages", "journalImpactFactor");
    }

    public static int getTotalNumberOfAuthors() {
        return totalNumberOfAuthors;
    }

    /**
     * Returns true when this publication should be added to the output series.
     */
    public boolean shouldBeAddedToSeries() {
        return year == 2010 && year < 2014;
    }

    public void toCsvWithoutAuthors(CSVPrinter csv) throws IOException {
        csv.printRecord(id, title, year, type, key, journalKey, journal,
                numPages, journalImpactFactor);
    }

    @Override
    public String toString() {
        return "Publication [number=" + id + ", title=" + title + ", year="
                + year + ", publisher=" + publisher + ", authors=" + authors
                + ", type=" + type + ", key=" + key + ", crossref=" + crossref
                + ", journal=" + journal + ", journalImpactFactor=" + journalImpactFactor + "]";
    }

    public final String getName() {
        return title;
    }

    public String getCrossref() {
        return crossref;
    }

    public int getYear() {
        return year;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public final int getId() {
        return this.id;
    }

    public PublicationType getType() {
        return type;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTitle() {
        return title;
    }

    public String getJournal() {
        return journal;
    }

    public String getJournalKey() {
        return journalKey;
    }

    public Integer getNumPages() {
        return numPages;
    }

    public Double getJournalImpactFactor() {
        return journalImpactFactor;
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
		Publication other = (Publication) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getKey() {
		return key;
	}

    public static final class Builder {
        private String title;
        private int year;
        private String publisher;
        private String journal;
        private List<String> authorNames = new ArrayList<>();
        private PublicationType type;
        private String key;
        private String journalKey;
        private String crossref;
        private Integer numPages;
        private Double journalImpactFactor;

        public String getJournal() {
            return journal;
        }

        public Builder setJournal(String s) {
            journal = s;
            return this;
        }

        public Double getJournalImpactFactor() {
            return journalImpactFactor;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public Builder setCrossref(String cr) {
            crossref = cr;
            return this;
        }

        public Builder setNumPages(Integer s) {
            numPages = s;
            return this;
        }

        public Builder setJournalKey(String s) {
            journalKey = s;
            return this;
        }

        public void addAuthorByName(String authorName) {
            this.authorNames.add(authorName);
        }

        public void setPublisher(String publisherName) {
            this.publisher = publisherName.replaceAll("\\s+", " ").trim();
        }

        public Builder setType(PublicationType type) {
            this.type = type;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setJournalImpactFactor(Double journalImpactFactor) {
            this.journalImpactFactor = journalImpactFactor;
            return this;
        }


        public Publication build(Map<String, Author> authorsByName) {
            List<Author> authors = authorNames.stream()
                    .map(name -> authorsByName.get(name))
                    .collect(Collectors.toList());

            Publication p = new Publication(title, year, publisher,
                    ImmutableList.copyOf(authors), type, key, journalKey,
                    crossref, journal, numPages, journalImpactFactor);

            for (Author a : authors)
                a.addPublication(p);
            return p;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title.replaceAll("\\s+", " ").trim();
        }
    }
}

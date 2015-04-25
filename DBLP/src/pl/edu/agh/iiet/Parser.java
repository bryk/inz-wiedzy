package pl.edu.agh.iiet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.edu.agh.iiet.model.Author;
import pl.edu.agh.iiet.model.Dblp;
import pl.edu.agh.iiet.model.Publication;
import pl.edu.agh.iiet.model.PublicationType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Parser {
	public static Dblp getDblpGraphFromFile(File input)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		Handler handler = new Handler();

		saxParser.parse(input, handler);
		System.out.println("Done parsing");
		Dblp ret = handler.computeDblpGraph();
		System.out.println("Done creating graph");
		return ret;
	}

	private static final class Handler extends DefaultHandler {
		private Map<String, Author.Builder> authorBuildersByName = new HashMap<>();
		private List<Publication.Builder> publicationBuilders = new ArrayList<>();
		private StringBuilder strBuilder;
		private Publication.Builder currentPublicationBuilder;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (currentPublicationBuilder != null) {
				if ("author".equals(qName) || "title".equals(qName)
						|| "year".equals(qName) || "publisher".equals(qName)
						|| "crossref".equals(qName) || "journal".equals(qName)) {
					strBuilder = new StringBuilder();
				}
			} else if (pubTypes.containsKey(qName)) {
				currentPublicationBuilder = Publication.builder().setType(
						pubTypes.get(qName));
				currentPublicationBuilder.setKey(attributes.getValue("key"));
				publicationBuilders.add(currentPublicationBuilder);
				if (publicationBuilders.size() % 1000 == 0) {
					System.out.println("Publication: " + publicationBuilders.size());
				}
			}
		}

		Map<String, PublicationType> pubTypes = ImmutableMap
				.<String, PublicationType> of(
						"article", PublicationType.ARTICLE);

		@Override
		public void characters(char[] chars, int start, int length)
				throws SAXException {
			if (strBuilder != null) {
				String str = new String(chars, start, length);
				strBuilder.append(str);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (currentPublicationBuilder != null) {
				if ("author".equals(qName)) {
					String name = strBuilder.toString();
					authorBuildersByName.computeIfAbsent(name, foo -> Author
							.builder().setName(name));
					currentPublicationBuilder.addAuthorByName(name);
					strBuilder = null;
				} else if ("title".equals(qName)) {
					currentPublicationBuilder.setTitle(strBuilder.toString());
					strBuilder = null;
				} else if ("year".equals(qName)) {
					currentPublicationBuilder.setYear(Integer
							.parseInt(strBuilder.toString()));
					strBuilder = null;
				} else if ("journal".equals(qName)) {
					currentPublicationBuilder.setJournal(strBuilder
							.toString());
					strBuilder = null;
				} else if ("publisher".equals(qName)) {
					currentPublicationBuilder.setPublisher(strBuilder
							.toString());
					strBuilder = null;
				} else if ("crossref".equals(qName)) {
					currentPublicationBuilder
							.setCrossref(strBuilder.toString());
					strBuilder = null;
				}
			}
			if (pubTypes.containsKey(qName)) {
				currentPublicationBuilder = null;
			}
		}

		public Dblp computeDblpGraph() {
			Map<String, Author> authorsByName = authorBuildersByName
					.entrySet()
					.stream()
					.collect(
							Collectors.toMap(
									Map.Entry<String, Author.Builder>::getKey,
									e -> e.getValue().build()));

			ImmutableList<Author> allAuthors = ImmutableList
					.<Author> copyOf(authorsByName.entrySet().stream()
							.map(e -> e.getValue())
							.collect(Collectors.toList()));

			ImmutableList<Publication> allPublications = ImmutableList
					.<Publication> copyOf(publicationBuilders.stream()
							.map(e -> e.build(authorsByName))
							.collect(Collectors.toList()));

			return new Dblp(allAuthors, allPublications, authorsByName);
		}
	}
}

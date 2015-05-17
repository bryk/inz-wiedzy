package pl.edu.agh.iiet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import pl.edu.agh.iiet.matcher.JcrDblpJorunalTitleMatcher;
import pl.edu.agh.iiet.model.*;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
	private static final Pattern JOURNAL_KEY_PATTERN = Pattern
			.compile("journals\\/(\\S+)/\\S+");
	private static final Pattern PAGES_PATTERN = Pattern
			.compile("(\\d+)-(\\d+)");

	public static Dblp getDblpGraphFromFile(File input, List<MinistryListEntryJCREntryPair> jcrAndMinistryData)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		Handler handler = new Handler();

		saxParser.parse(input, handler);
		System.out.println("Done parsing");
		Dblp ret = handler.computeDblpGraph(jcrAndMinistryData);
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
						|| "crossref".equals(qName) || "journal".equals(qName)
						|| "pages".equals(qName)) {
					strBuilder = new StringBuilder();
				}
			} else if (pubTypes.containsKey(qName)) {
				currentPublicationBuilder = Publication.builder().setType(
						pubTypes.get(qName));
				String key = attributes.getValue("key");
				currentPublicationBuilder.setKey(key);
				Matcher match = JOURNAL_KEY_PATTERN.matcher(key);
				if (match.matches()) {
					String journalKey = match.group(1);
					currentPublicationBuilder.setJournalKey(journalKey);
				}
				publicationBuilders.add(currentPublicationBuilder);
				if (publicationBuilders.size() % 1000 == 0) {
					System.out.println("Publication: "
							+ publicationBuilders.size());
				}
			}
		}

		Map<String, PublicationType> pubTypes = ImmutableMap
				.<String, PublicationType> of("article",
						PublicationType.ARTICLE);

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
				} else if ("pages".equals(qName)) {
					String pages = strBuilder.toString();
					Matcher matcher = PAGES_PATTERN.matcher(pages);
					if (matcher.matches()) {
						Integer from = Integer.parseInt(matcher.group(1));
						Integer to = Integer.parseInt(matcher.group(2));
						currentPublicationBuilder.setNumPages(to - from);
					}
					strBuilder = null;
				} else if ("journal".equals(qName)) {
					currentPublicationBuilder.setJournal(strBuilder.toString());

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

		private void addJCRandMinistryData(List<MinistryListEntryJCREntryPair> jcrAndMinistryData) {
			if (jcrAndMinistryData != null) {
				Map<String, MinistryListEntryJCREntryPair> jcrEntryMap = jcrAndMinistryData.stream()
						.collect(Collectors.toMap(pair -> pair.getJcrEntry().getTitle(), Function.identity()));
				Set<String> jcrTitles = jcrEntryMap.keySet();
				JcrDblpJorunalTitleMatcher titleMatcher = new JcrDblpJorunalTitleMatcher(jcrTitles);
				publicationBuilders.forEach(builder -> {
					String dblpTitle = Optional.ofNullable(builder.getJournal()).orElse("");
					String matchedJcrTitle = titleMatcher.findBestMatch(dblpTitle);
					MinistryListEntryJCREntryPair pair = jcrEntryMap.get(matchedJcrTitle);
					if (pair != null) {
						builder.setJournalImpactFactor(pair.getImpactFactor());
						builder.setMinistryPoints(pair.getMinistryPoints());
					}
				});
				titleMatcher.saveMatchesToCsv();
			}
		}

		public Dblp computeDblpGraph(List<MinistryListEntryJCREntryPair> jcrAndMinistryData) {
			addJCRandMinistryData(jcrAndMinistryData);
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

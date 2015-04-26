package pl.edu.agh.ztis.jcr.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class JCRParser implements Parser<JCREntry> {

    @Override
    public List<JCREntry> parse(String pageContent) {
        Document document = Jsoup.parse(pageContent);
        String TABLE_SELECTOR = "table[width=90%]";
        Element table = document.select(TABLE_SELECTOR).first();
        Elements dataRows = table.select("tr:gt(1)");
        return dataRows.stream().map(this::parseElement).collect(toList());
    }

    private JCREntry parseElement(Element element) {
        List<String> data = element.select("td:gt(0)").stream().map(Element::text).collect(toList());
        return createJCREntryFromList(data);
    }

    private JCREntry createJCREntryFromList(List<String> data) {
        if (data.size() == 11) {
            JCREntry jcrEntry = new JCREntry.JCREntryBuilder()
                    .withRank((data.get(0)))
                    .withTitle(data.get(1))
                    .withISSN(data.get(2))
                    .withTotalCites(data.get(3))
                    .withImpactFactor((data.get(4)))
                    .withFiveYearImpactFactor(data.get(5))
                    .withImmediacyIndex((data.get(6)))
                    .withArticleCount((data.get(7)))
                    .withCitedHalfLife(data.get(8))
                    .withEigenfactorScore((data.get(9)))
                    .withArticleInfluenceScore((data.get(10)))
                    .build();
            return jcrEntry;
        } else {
            throw new RuntimeException();
        }
    }
}
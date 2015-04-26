package pl.edu.agh.ztis.jcr.parser;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class JCRParserTest {

    private JCRParser parser = new JCRParser();

    private String sampleJcrHtml;

    @Before
    public void setUp() throws Exception {
        sampleJcrHtml = String.join("", Files.readAllLines(Paths.get("src/test/resources/sample_page.html")));
    }

    @Test
    public void testParse() throws Exception {
        JCREntry expected = new JCREntry.JCREntryBuilder()
                .withRank(1321)
                .withTotalCites(12565)
                .withTitle("BMC CANCER")
                .withISSN("1471-2407")
                .withImpactFactor(3.319)
                .withFiveYearImpactFactor(3.640)
                .withImmediacyIndex(0.370)
                .withArticleCount(616)
                .withCitedHalfLife("3.8")
                .withEigenfactorScore(0.04760)
                .withArticleInfluenceScore(1.065)
                .build();


        List<JCREntry> parsed = parser.parse(sampleJcrHtml);

        assertThat(parsed).hasSize(20);
        assertThat(parsed.get(0)).isEqualTo(expected);


    }
}
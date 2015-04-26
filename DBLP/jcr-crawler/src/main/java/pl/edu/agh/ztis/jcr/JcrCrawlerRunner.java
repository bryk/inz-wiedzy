package pl.edu.agh.ztis.jcr;

import pl.edu.agh.ztis.jcr.crawler.JCRCrawler;
import pl.edu.agh.ztis.jcr.model.JCREntry;
import pl.edu.agh.ztis.jcr.parser.JCRParser;
import pl.edu.agh.ztis.jcr.parser.Parser;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class JcrCrawlerRunner {
    private static final int STEP = 20;
    private static final int JOURNAL_COUNT = 8539;
    private static JcrCrawlerRunner runner;
    private Parser<JCREntry> parser = new JCRParser();
    private JCRCrawler crawler = new JCRCrawler();

    private JcrCrawlerRunner() {
    }

    public static void main(String[] args) {
        JcrCrawlerRunner.getInstance().run();
    }

    public static JcrCrawlerRunner getInstance() {
        if (runner == null) {
            runner = new JcrCrawlerRunner();
        }
        return runner;
    }

    public List<JCREntry> run(int jcrPageLimit) {
        List<JCREntry> jcrEntries = IntStream.iterate(1, n -> n + STEP).limit(jcrPageLimit)
                .parallel().mapToObj(crawler::crawlOnePage).flatMap(s -> parser.parse(s).stream()).collect(toList());
        return jcrEntries;
    }

    public List<JCREntry> run() {
        int maxSize = JOURNAL_COUNT / STEP + 1;
        return run(maxSize);
    }

}

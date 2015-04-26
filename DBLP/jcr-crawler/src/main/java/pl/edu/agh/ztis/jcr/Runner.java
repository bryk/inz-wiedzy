package pl.edu.agh.ztis.jcr;

import pl.edu.agh.ztis.jcr.crawler.JCRCrawler;
import pl.edu.agh.ztis.jcr.model.JCREntry;
import pl.edu.agh.ztis.jcr.parser.JCRParser;
import pl.edu.agh.ztis.jcr.parser.Parser;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Runner {
    private static final int STEP = 20;
    private static final int JOURNAL_COUNT = 8539;
    private Parser<JCREntry> parser = new JCRParser();
    private JCRCrawler crawler = new JCRCrawler();

    public static void main(String[] args) {
        new Runner().run();
    }

    public List<JCREntry> run() {
        int maxSize = JOURNAL_COUNT / STEP + 1;
        List<JCREntry> collect = IntStream.iterate(1, n -> n + STEP).limit(maxSize)
                .parallel().mapToObj(crawler::crawlOnePage).flatMap(s -> parser.parse(s).stream()).collect(toList());
        return collect;
    }

}

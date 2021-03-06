package pl.edu.agh.iiet.matcher;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.tuple.Pair;
import pl.edu.agh.iiet.util.DiceCoefficientSimilarityProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class JcrDblpJorunalTitleMatcher {

    private static final double MINIMAL_SIMILARITY = 0.72;
    private static final Pair<String, Double> MATCH_NOT_FOUND = Pair.of("MATCH_NOT_FOUND", 0.0);
    private DiceCoefficientSimilarityProvider similarityProvider = new DiceCoefficientSimilarityProvider();

    private final Map<String, Pair<String, Double>> matchesCache = new HashMap<>();
    private final Set<String> potentialMatches;

    public JcrDblpJorunalTitleMatcher(Set<String> potentialMatches) {
        this.potentialMatches = potentialMatches;
    }


    public String findBestMatch(String target) {
        Pair<String, Double> matchedPair = matchesCache.get(target);
        if (matchedPair == null) {
            String sanitizedTarget = sanitized(target);
            List<Pair<String, Double>> bestMatches = potentialMatches.parallelStream()
                    .map(potentialMatch -> Pair.of(potentialMatch, similarityProvider.computeSimilarity(sanitized(potentialMatch), sanitizedTarget))
                    ).sorted((pair1, pair2) ->
                            Double.compare(pair1.getValue(), pair2.getValue())).
                            filter(pair ->
                                    pair.getValue() > MINIMAL_SIMILARITY).collect(toList());

            matchedPair = bestMatches.size() > 0 ? bestMatches.get(0) : MATCH_NOT_FOUND;
            matchesCache.put(target, matchedPair);
            System.out.println(String.format("Match #%d. %s with %s.", matchesCache.size(), target, matchedPair.getKey()));
        }
        return matchedPair.getKey();
    }

    private String sanitized(String toSanitize) {
        return toSanitize.toLowerCase().replace(".", "").replace("\\s+", "");
    }

    public void saveMatchesToCsv() {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter("dblp-jcr-matched-titles.csv"), CSVFormat.EXCEL)) {
            printer.printRecord("dblp_title", "jcr_title", "similarity");
            for (Map.Entry<String, Pair<String, Double>> entry : matchesCache.entrySet()) {
                Pair<String, Double> pair = entry.getValue();
                if (pair.getValue() >= MINIMAL_SIMILARITY) {
                    printer.printRecord(entry.getKey(), pair.getKey(), pair.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package pl.edu.agh.iiet.mapper;

import pl.edu.agh.iiet.util.DiceCoefficientSimilarityProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JcrDblpJorunalTitleMatcher {

    private DiceCoefficientSimilarityProvider diceCoefficientSimilarityProvider = new DiceCoefficientSimilarityProvider();

    public final Map<String, String> matchesCache = new HashMap<>();
    private final List<String> potentialMatches;

    public JcrDblpJorunalTitleMatcher(List<String> potentialMatches) {
        this.potentialMatches = potentialMatches;
    }


    public String findBestMatch(String target) {
        String matched = matchesCache.get(target);
        if (matched == null) {
            String lowerCaseTarget = target.toLowerCase();
            List<String> bestMatches = potentialMatches.parallelStream().sorted((s2, s1) -> Double.compare(
                    diceCoefficientSimilarityProvider.computeSimilarity(lowerCaseTarget, s1.toLowerCase()),
                    diceCoefficientSimilarityProvider.computeSimilarity(lowerCaseTarget, s2.toLowerCase())
            )).collect(toList());
            matched = bestMatches.size() > 0 ? bestMatches.get(0) : "";
            matchesCache.put(target, matched);
            System.out.println(String.format("Match #%d. %s with %s.", matchesCache.size(), target, matched));
        }
        return matched;
    }
}

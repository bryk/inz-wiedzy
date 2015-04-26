package pl.edu.agh.iiet.mapper;

import pl.edu.agh.iiet.util.DiceCoefficientSimilarityProvider;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class JcrDblpJorunalTitleMatcher {

    private DiceCoefficientSimilarityProvider diceCoefficientSimilarityProvider = new DiceCoefficientSimilarityProvider();

    //TODO cache
    public String findBestMatch(String target, List<String> potentialMatches) {
        String lowerCaseTarget = target.toLowerCase();
        List<String> bestMatches = potentialMatches.stream().sorted((s2, s1) -> Double.compare(
                diceCoefficientSimilarityProvider.computeSimilarity(lowerCaseTarget, s1.toLowerCase()),
                diceCoefficientSimilarityProvider.computeSimilarity(lowerCaseTarget, s2.toLowerCase())
        )).collect(toList());
        return bestMatches.size() > 0 ? bestMatches.get(0) : "";
    }
}

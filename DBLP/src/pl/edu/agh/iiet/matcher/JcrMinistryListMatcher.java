package pl.edu.agh.iiet.matcher;

import com.google.common.collect.Sets;
import pl.edu.agh.iet.model.MinistryListEntry;
import pl.edu.agh.iiet.model.MinistryListEntryJCREntryPair;
import pl.edu.agh.ztis.jcr.model.JCREntry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class JcrMinistryListMatcher {

    public static final String BRAK = "Brak";

    public List<MinistryListEntryJCREntryPair> match(List<JCREntry> jcrEntries, List<MinistryListEntry> ministryListEntries) {
        Map<String, JCREntry> jcrEntriesByISSN = jcrEntries.stream()
                .collect(Collectors.groupingBy(JCREntry::getISSN))
                .entrySet().stream().filter(entry -> entry.getValue().size() == 1)
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));

        Map<String, MinistryListEntry> ministryListEntriesByISSN =
                ministryListEntries.stream().distinct()
                        .filter(ministryListEntry -> !ministryListEntry.getISSN().equals(BRAK))
                        .collect(toMap(MinistryListEntry::getISSN, Function.identity()));

        Sets.SetView<String> commonISSNs = Sets.intersection(jcrEntriesByISSN.keySet(), ministryListEntriesByISSN.keySet());
        List<MinistryListEntryJCREntryPair> matchedPairs = commonISSNs.stream().map(commonISSN -> new MinistryListEntryJCREntryPair(
                ministryListEntriesByISSN.get(commonISSN),
                jcrEntriesByISSN.get(commonISSN)
        )).collect(toList());
        return matchedPairs;
    }
}

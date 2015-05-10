package main.java.pl.edu.agh.iet;

import main.java.pl.edu.agh.iet.model.MinistryListEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MinistryListParser {

    public static final String FULL_ENTRY_LINE = "^[0-9].*";
    public static final int TITLE_INDEX = 1;

    public List<MinistryListEntry> parse(String file) throws IOException {
        return Files.readAllLines(Paths.get(file)).stream()
                .map(new PreviousLineAwareSplittingFunction())
                .filter(list -> !list.isEmpty())
                .map(MinistryListEntry::fromList)
                .collect(toList());
    }

    private static class PreviousLineAwareSplittingFunction implements Function<String, List<String>> {
        private Optional<String> previous = Optional.empty();

        @Override
        public List<String> apply(String current) {
            List<String> result = null;
            if (!current.matches(FULL_ENTRY_LINE)) {
                previous = Optional.of(current);
            } else {
                result = Stream.of(current.split("\\s{2,}")).map(String::trim).collect(toList());
                modifyTitleIfNeeded(result);
                previous = Optional.empty();
            }
            return Optional.ofNullable(result).orElse(Collections.emptyList());
        }

        private void modifyTitleIfNeeded(List<String> result) {
            if (previous.isPresent()) {
                String preparedPrevious = previous.get().trim().replace("\n", "");
                String joined = String.join(" ", preparedPrevious, result.get(TITLE_INDEX));
                result.set(TITLE_INDEX, joined);
            }
        }
    }
}

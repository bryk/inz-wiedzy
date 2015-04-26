package pl.edu.agh.ztis.jcr.parser;

import java.util.List;

public interface Parser<T> {
    List<T> parse(String pageContent);
}

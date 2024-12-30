package com.etl.sample;

import java.util.List;

public class ParsedResult {
    public final List<Author> authors;
    public final List<Affiliation> affiliations;

    public ParsedResult(List<Author> authors, List<Affiliation> affiliations) {
        this.authors = authors;
        this.affiliations = affiliations;
    }
}

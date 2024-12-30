package com.journal.crawler.knowledgegraph.model;

import lombok.Data;

@Data
public class Edge {
    private Integer from;
    private Integer to;
    private String label;

    public Edge(Integer from, Integer to, String label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }
}
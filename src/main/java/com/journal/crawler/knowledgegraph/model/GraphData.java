package com.journal.crawler.knowledgegraph.model;

import lombok.Data;

import java.util.List;

@Data
public class GraphData {
    private List<Node> nodes;
    private List<Edge> edges;
}
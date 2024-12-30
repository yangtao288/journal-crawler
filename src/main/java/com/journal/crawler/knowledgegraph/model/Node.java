package com.journal.crawler.knowledgegraph.model;

import lombok.Data;

@Data
public class Node {
    private Integer id;
    private String label;
    private String group;

    public Node() {}

    public Node(Integer id, String label, String group) {
        this.id = id;
        this.label = label;
        this.group = group;
    }
}
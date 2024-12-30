package com.journal.crawler.knowledge.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Relationship {
    private Long source;
    private Long target;
    private String type;
}
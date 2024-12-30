package com.journal.crawler.knowledge.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node {
    private Long id;
    private String name;
    private String type; // PERSON, SCHOOL, PROJECT
    private Integer x;
    private Integer y;
}
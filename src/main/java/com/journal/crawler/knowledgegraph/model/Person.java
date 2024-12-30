package com.journal.crawler.knowledgegraph.model;

import lombok.Data;

@Data
public class Person {
    private String name;
    private String organization;  // 单位
    private String title;  // 职称
}

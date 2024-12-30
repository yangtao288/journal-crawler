package com.journal.crawler.knowledgegraph.model;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDoc {
    private String projectId;
    private String projectName;
    private Person leader;  // 项目负责人
    private List<Person> members;  // 项目成员
    private List<ProjectDoc> similarProject;  // 相似文档
}

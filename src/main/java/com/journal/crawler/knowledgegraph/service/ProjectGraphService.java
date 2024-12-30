package com.journal.crawler.knowledgegraph.service;

import com.journal.crawler.knowledgegraph.model.GraphData;
import com.journal.crawler.knowledgegraph.model.Person;
import com.journal.crawler.knowledgegraph.model.ProjectDoc;
import com.journal.crawler.knowledgegraph.util.ProjectGraphParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProjectGraphService {

    @Autowired
    private ProjectGraphParser graphParser;

    public GraphData generateProjectGraph(List<ProjectDoc> docs) {
        return graphParser.parseProjectDocs(docs);
    }

    // 示例数据生成方法
    public ProjectDoc generateSampleData() {
        // 创建项目A：智能交通系统
        ProjectDoc docA = new ProjectDoc();
        docA.setProjectId("A");
        docA.setProjectName("智能交通系统");

        Person leaderA = new Person();
        leaderA.setName("张三");
        leaderA.setOrganization("清华大学");
        leaderA.setTitle("教授");
        docA.setLeader(leaderA);

        List<Person> membersA = new ArrayList<>();
        membersA.add(createPerson("李四", "清华大学", "副教授"));
        membersA.add(createPerson("王五", "清华大学", "讲师"));
        membersA.add(createPerson("赵六", "北京大学", "研究员"));
        membersA.add(createPerson("钱七", "北京大学", "副研究员"));
        docA.setMembers(membersA);

        // 创建项目B：智慧城市监控平台
        ProjectDoc docB = new ProjectDoc();
        docB.setProjectId("B");
        docB.setProjectName("智慧城市监控平台");

        Person leaderB = new Person();
        leaderB.setName("孙八");
        leaderB.setOrganization("北京大学");
        leaderB.setTitle("教授");
        docB.setLeader(leaderB);

        List<Person> membersB = new ArrayList<>();
        membersB.add(createPerson("王五", "清华大学", "讲师"));
        membersB.add(createPerson("张三", "清华大学", "教授"));
        membersB.add(createPerson("赵六", "北京大学", "研究员")); // 与项目A重叠的成员
        membersB.add(createPerson("郑十一", "清华大学", "副研究员"));
        membersB.add(createPerson("初五", "清华大学", "博士"));
        docB.setMembers(membersB);

        // 创建项目C：城市大数据分析平台
        ProjectDoc docC = new ProjectDoc();
        docC.setProjectId("C");
        docC.setProjectName("城市大数据分析平台");

        Person leaderC = new Person();
        leaderC.setName("王五"); // 项目A的成员作为项目C的负责人
        leaderC.setOrganization("清华大学");
        leaderC.setTitle("讲师");
        docC.setLeader(leaderC);

        List<Person> membersC = new ArrayList<>();
        membersC.add(createPerson("李四", "清华大学", "副教授")); // 与项目A重叠的成员
        membersC.add(createPerson("张三", "清华大学", "副研究员"));
        membersC.add(createPerson("钱七", "北京大学", "副研究员")); // 与项目A重叠的成员
        membersC.add(createPerson("陈十二", "浙江大学", "教授")); // 新增院校的成员
        docC.setMembers(membersC);

        docA.setSimilarProject(Arrays.asList(docC, docB));
        return docA;
    }

    // 辅助方法：创建人员对象
    private Person createPerson(String name, String organization, String title) {
        Person person = new Person();
        person.setName(name);
        person.setOrganization(organization);
        person.setTitle(title);
        return person;
    }
}
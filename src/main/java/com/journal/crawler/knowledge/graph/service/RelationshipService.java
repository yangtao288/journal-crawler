package com.journal.crawler.knowledge.graph.service;

import com.journal.crawler.knowledge.graph.model.Node;
import com.journal.crawler.knowledge.graph.model.Relationship;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RelationshipService {

    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        // 这里可以从数据库获取数据
        // 示例数据
        nodes.add(new Node(1L, "张三", "PERSON", 400, 150));
        nodes.add(new Node(2L, "李四", "PERSON", 200, 300));
        nodes.add(new Node(3L, "王五", "PERSON", 600, 300));
        nodes.add(new Node(4L, "赵六", "PERSON", 300, 450));
        nodes.add(new Node(5L, "钱七", "PERSON", 500, 450));
        nodes.add(new Node(6L, "孙八", "PERSON", 400, 300));
        nodes.add(new Node(7L, "清华大学", "SCHOOL", 700, 200));
        nodes.add(new Node(8L, "北京大学", "SCHOOL", 150, 200));
        nodes.add(new Node(9L, "AI项目组", "PROJECT", 300, 600));
        nodes.add(new Node(10L, "大数据项目组", "PROJECT", 700, 600));
        return nodes;
    }

    public List<Relationship> getAllRelationships() {
        List<Relationship> relationships = new ArrayList<>();
        // 这里可以从数据库获取数据
        // 示例数据
        relationships.add(new Relationship(1L, 7L, "就读于"));
        relationships.add(new Relationship(3L, 7L, "就读于"));

        relationships.add(new Relationship(2L, 8L, "就读于"));
        relationships.add(new Relationship(6L, 8L, "任教于"));

        relationships.add(new Relationship(4L, 9L, "参与"));
        relationships.add(new Relationship(5L, 9L, "参与"));
        relationships.add(new Relationship(1L, 10L, "负责"));
        relationships.add(new Relationship(3L, 10L, "负责"));

        relationships.add(new Relationship(4L, 5L, "同学"));
        relationships.add(new Relationship(1L, 2L, "同事"));
        return relationships;
    }
}
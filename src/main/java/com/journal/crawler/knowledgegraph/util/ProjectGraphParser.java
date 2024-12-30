package com.journal.crawler.knowledgegraph.util;

import com.journal.crawler.knowledgegraph.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProjectGraphParser {

    public GraphData parseProjectDocs(List<ProjectDoc> projectDocs) {
        GraphData graphData = new GraphData();
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // 用于去重和ID映射
        Map<String, Integer> nodeIdMap = new HashMap<>();
        int currentId = 1;

        for (ProjectDoc doc : projectDocs) {
            // 1. 添加项目节点
            Integer projectNodeId = addNode(nodes, nodeIdMap, currentId++,
                    doc.getProjectName(), "project");

            // 2. 添加负责人节点和关系
            Integer leaderNodeId = addPersonNode(nodes, nodeIdMap, currentId++, doc.getLeader());
            edges.add(new Edge(leaderNodeId, projectNodeId, "负责"));

            // 3. 添加负责人单位节点和关系
            Integer leaderOrgNodeId = addNode(nodes, nodeIdMap, currentId++,
                    doc.getLeader().getOrganization(), "organization");
            edges.add(new Edge(leaderNodeId, leaderOrgNodeId, "所属"));

            // 4. 添加项目成员节点和关系
            for (Person member : doc.getMembers()) {
                Integer memberNodeId = addPersonNode(nodes, nodeIdMap, currentId++, member);
                edges.add(new Edge(memberNodeId, projectNodeId, "参与"));

                // 添加成员单位节点和关系
                Integer orgNodeId = addNode(nodes, nodeIdMap, currentId++,
                        member.getOrganization(), "organization");
                edges.add(new Edge(memberNodeId, orgNodeId, "所属"));

                // 添加成员之间的同事关系
                for (Person otherMember : doc.getMembers()) {
                    if (!member.getName().equals(otherMember.getName()) &&
                            member.getOrganization().equals(otherMember.getOrganization())) {
                        Integer otherMemberNodeId = nodeIdMap.get(otherMember.getName());
                        edges.add(new Edge(memberNodeId, otherMemberNodeId, "同事"));
                    }
                }
            }
        }

        graphData.setNodes(nodes);
        graphData.setEdges(edges);
        return graphData;
    }

    private Integer addNode(List<Node> nodes, Map<String, Integer> nodeIdMap,
                            int currentId, String label, String group) {
        if (nodeIdMap.containsKey(label)) {
            return nodeIdMap.get(label);
        }

        Node node = new Node();
        node.setId(currentId);
        node.setLabel(label);
        node.setGroup(group);
        nodes.add(node);
        nodeIdMap.put(label, currentId);
        return currentId;
    }

    private Integer addPersonNode(List<Node> nodes, Map<String, Integer> nodeIdMap,
                                  int currentId, Person person) {
        String label = person.getName() + "\n" + person.getTitle();
        return addNode(nodes, nodeIdMap, currentId, label, "person");
    }

    private ProjectDoc findProjectById(List<ProjectDoc> docs, String projectId) {
        return docs.stream()
                .filter(doc -> doc.getProjectId().equals(projectId))
                .findFirst()
                .orElse(null);
    }
}

package com.journal.crawler.knowledgegraph.service;

import com.journal.crawler.knowledgegraph.model.Edge;
import com.journal.crawler.knowledgegraph.model.GraphData;
import com.journal.crawler.knowledgegraph.model.Node;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GraphService {

    public GraphData getGraphData() {
        GraphData data = new GraphData();

        data.setNodes(Arrays.asList(
                new Node(1, "张三", "person"),
                new Node(2, "李四", "person"),
                new Node(3, "王五", "person"),
                new Node(4, "赵六", "person"),
                new Node(5, "钱七", "person"),
                new Node(6, "孙八", "person"),
                new Node(7, "清华大学", "school"),
                new Node(8, "智能系统项目组", "project")
        ));

        data.setEdges(Arrays.asList(
                new Edge(1, 2, "同事"),
                new Edge(1, 7, "就读于"),
                new Edge(2, 7, "任教于"),
                new Edge(3, 4, "同学"),
                new Edge(4, 5, "合作"),
                new Edge(5, 8, "参与"),
                new Edge(6, 8, "参与")
        ));

        return data;
    }
}

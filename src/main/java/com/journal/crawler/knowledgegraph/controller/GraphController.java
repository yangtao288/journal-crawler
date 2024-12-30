package com.journal.crawler.knowledgegraph.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.crawler.knowledgegraph.model.ProjectDoc;
import com.journal.crawler.knowledgegraph.service.GraphService;
import com.journal.crawler.knowledgegraph.service.ProjectGraphService;
import com.journal.crawler.knowledgegraph.util.RelationshipParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @Autowired
    private ObjectMapper objectMapper;  // Spring Boot 自动配置的 ObjectMapper

    @Autowired
    private ProjectGraphService projectGraphService;

    @GetMapping("/show")
    public String showGraph(Model model) throws JsonProcessingException {
        // 获取示例数据
        ProjectDoc doc = projectGraphService.generateSampleData();

        // 生成图谱数据
        RelationshipParser parser = new RelationshipParser();
        parser.parseRelationships(doc);

        // 将数据转换为 JSON 字符串
        String nodesJson = objectMapper.writeValueAsString(parser.getNodes());
        String edgesJson = objectMapper.writeValueAsString(parser.getEdges());

        model.addAttribute("nodesJson", nodesJson);
        model.addAttribute("edgesJson", edgesJson);
        return "graph";
    }
}

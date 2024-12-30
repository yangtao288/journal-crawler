package com.journal.crawler.knowledge.graph.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.crawler.knowledge.graph.model.Node;
import com.journal.crawler.knowledge.graph.model.Relationship;
import com.journal.crawler.knowledge.graph.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/graph")
public class KnowledgeGraphController {

    @Autowired
    private RelationshipService relationshipService; // 假设有一个服务类处理数据

    @GetMapping("/view")
    public String showGraph(Model model) {
        // 构建节点数据
        List<Node> nodes = relationshipService.getAllNodes();
        List<Relationship> relationships = relationshipService.getAllRelationships();

        // 转换为JSON字符串
        ObjectMapper mapper = new ObjectMapper();
        try {
            model.addAttribute("nodes", mapper.writeValueAsString(nodes));
            model.addAttribute("relationships", mapper.writeValueAsString(relationships));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "knowledge-graph";
    }
}
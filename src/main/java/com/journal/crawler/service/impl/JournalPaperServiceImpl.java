package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.journal.crawler.entity.JournalExecutePlan;
import com.journal.crawler.entity.JournalIssue;
import com.journal.crawler.entity.JournalIssueRule;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.enums.GrabStatusEnum;
import com.journal.crawler.enums.JournalExecuteHistoryPhaseEnum;
import com.journal.crawler.mapper.JournalPaperMapper;
import com.journal.crawler.service.JournalExecuteHistoryService;
import com.journal.crawler.service.JournalIssueRuleService;
import com.journal.crawler.service.JournalIssueService;
import com.journal.crawler.service.JournalPaperService;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PaperLinkCrawlerStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class JournalPaperServiceImpl extends
        ServiceImpl<JournalPaperMapper, JournalPaper> implements JournalPaperService {

    @Autowired
    private JournalIssueRuleService issueRuleService;
    @Autowired
    private JournalExecuteHistoryService executeHistoryService;
    @Autowired
    private JournalIssueService journalIssueService;
    @Autowired
    private List<PaperLinkCrawlerStrategy> paperLinkCrawlerStrategies;
    private Map<BasicIdKeyEnum, PaperLinkCrawlerStrategy> paperLinkCrawlerStrategyMap;

    @PostConstruct
    public void init() {
        paperLinkCrawlerStrategyMap = Maps.newHashMap();
        for (PaperLinkCrawlerStrategy paperLinkCrawlerStrategy : paperLinkCrawlerStrategies) {
            paperLinkCrawlerStrategyMap.put(paperLinkCrawlerStrategy.basicIdKey(), paperLinkCrawlerStrategy);
        }
    }

    public List<JournalPaper> paperLinkCrawler(JournalExecutePlan plan) {
        BasicIdKeyEnum basicIdKeyEnum = BasicIdKeyEnum.convert(plan.getBasicId());
        List<JournalIssueRule> issueRules = issueRuleService.findByBasicId(plan.getBasicId());
        if (CollectionUtils.isEmpty(issueRules)) {
            return null;
        }

        JournalIssue journalIssue = journalIssueService.getById(plan.getIssueId());
        if (Objects.isNull(journalIssue)) {
            return null;
        }

        // 按issueUrlPrefix长度倒序排序
        issueRules.sort(Comparator.comparingInt(a -> a.getIssueUrlPrefix().length()));
        JournalIssueRule onlyOneRule = issueRules.stream().filter(
                s -> plan.getIssueUrl().contains(s.getIssueUrlPrefix())).findFirst().get();
        List<JournalPaper> papers = paperLinkCrawlerStrategyMap.get(
                basicIdKeyEnum).crawlPaperLinks(onlyOneRule, journalIssue.getIssueUrl());

        if (CollectionUtils.isEmpty(papers)) {
            // 更新卷期数据
            journalIssue.setGrabStatus(GrabStatusEnum.FAILED.name());
            journalIssue.setUpdated(new Date());
            journalIssueService.updateById(journalIssue);
            return null;
        }

        // 补全关键属性数据
        for (JournalPaper journalPaper : papers) {
            journalPaper.setBasicId(plan.getBasicId());
            journalPaper.setIssueId(plan.getIssueId());
            journalPaper.setCreated(new Date());
            journalPaper.setUpdated(new Date());
        }

        // 保存数据
        saveBatch(papers);

        // 记录抓取论文链接数据历史
        executeHistoryService.saveJournalExecuteHistoryRecord(
                plan, JournalExecuteHistoryPhaseEnum.PAPERS.name(), papers.size(), 0);

        // 更新卷期数据
        journalIssue.setGrabStatus(GrabStatusEnum.SUCCESS.name());
        journalIssue.setUpdated(new Date());
        journalIssueService.updateById(journalIssue);

        return papers;
    }
}

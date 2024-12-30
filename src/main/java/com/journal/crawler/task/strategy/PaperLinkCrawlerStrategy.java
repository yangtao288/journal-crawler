package com.journal.crawler.task.strategy;

import com.journal.crawler.entity.JournalIssueRule;
import com.journal.crawler.entity.JournalPaper;

import java.util.List;

/**
 * 论文链接抓取策略
 */
public interface PaperLinkCrawlerStrategy {

    BasicIdKeyEnum basicIdKey();

    List<JournalPaper> crawlPaperLinks(JournalIssueRule rule, String issueUrl);
}

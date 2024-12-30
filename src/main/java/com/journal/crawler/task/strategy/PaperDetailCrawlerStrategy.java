package com.journal.crawler.task.strategy;

import com.journal.crawler.entity.JournalBasic;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.entity.JournalPaperDetail;
import com.journal.crawler.entity.JournalPaperDetailRule;

/**
 * 论文详情抓取策略
 */
public interface PaperDetailCrawlerStrategy {

    BasicIdKeyEnum basicIdKey();

    /**
     * 论文链接前缀，识别一本期刊多种论文链接规则
     * @return
     */
    String paperUrlPrefix();

    JournalPaperDetail crawlPaperDetail(JournalBasic basic, JournalPaperDetailRule rule, JournalPaper paper);
}

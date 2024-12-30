package com.journal.crawler.task.strategy;

import com.journal.crawler.entity.JournalPaperAgency;
import com.journal.crawler.entity.JournalPaperAuthor;
import com.journal.crawler.entity.JournalPaperDetail;

import java.util.List;
import java.util.Map;

/**
 * 论文作者和机构清洗策略
 */
public interface PaperAuthorCleanStrategy {

    BasicIdKeyEnum basicIdKey();

    /**
     * 论文链接前缀，识别一本期刊多种论文链接规则
     * @return
     */
    String paperUrlPrefix();

    /**
     * 类样式、xPath、Script
     * @return
     */
    Map<String, String> expression();

    List<JournalPaperAuthor> cleanAuthors(JournalPaperDetail paperDetail);
    List<JournalPaperAgency> cleanAgencies(JournalPaperDetail paperDetail);
}

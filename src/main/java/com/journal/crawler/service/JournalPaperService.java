package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalExecutePlan;
import com.journal.crawler.entity.JournalPaper;

import java.util.List;

public interface JournalPaperService extends IService<JournalPaper> {
    List<JournalPaper> paperLinkCrawler(JournalExecutePlan plan);
}
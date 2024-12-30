package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalBasic;
import com.journal.crawler.entity.JournalExecutePlan;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.entity.JournalPaperDetail;

import java.util.List;

public interface JournalPaperDetailService extends IService<JournalPaperDetail> {
    List<JournalPaperDetail> paperDetailsCrawler(JournalBasic basic, JournalExecutePlan plan, List<JournalPaper> papers);

    void paperPdfDownload(JournalBasic basic, JournalExecutePlan plan, List<JournalPaperDetail> details);
}
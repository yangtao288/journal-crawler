package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalExecuteHistory;
import com.journal.crawler.entity.JournalExecutePlan;

public interface JournalExecuteHistoryService extends IService<JournalExecuteHistory> {
    void saveJournalExecuteHistoryRecord(JournalExecutePlan plan, String phase, Integer successNum, Integer failedNum);
}
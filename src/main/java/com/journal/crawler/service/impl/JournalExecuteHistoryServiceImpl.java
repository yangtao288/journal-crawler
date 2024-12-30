package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalExecuteHistory;
import com.journal.crawler.entity.JournalExecutePlan;
import com.journal.crawler.mapper.JournalExecuteHistoryMapper;
import com.journal.crawler.service.JournalExecuteHistoryService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JournalExecuteHistoryServiceImpl
        extends ServiceImpl<JournalExecuteHistoryMapper, JournalExecuteHistory> implements JournalExecuteHistoryService {

    public void saveJournalExecuteHistoryRecord(JournalExecutePlan plan, String phase, Integer successNum, Integer failedNum) {
        JournalExecuteHistory journalExecuteHistory = new JournalExecuteHistory();
        journalExecuteHistory.setPlanId(plan.getId());
        journalExecuteHistory.setBasicId(plan.getBasicId());
        journalExecuteHistory.setIssueId(plan.getIssueId());
        journalExecuteHistory.setIssueUrl(plan.getIssueUrl());
        journalExecuteHistory.setPhase(phase);
        journalExecuteHistory.setStatus("SUCCESS");
        journalExecuteHistory.setSuccessNum(successNum);
        journalExecuteHistory.setFailedNum(failedNum);
        journalExecuteHistory.setCreated(new Date());
        journalExecuteHistory.setUpdated(new Date());
        save(journalExecuteHistory);
    }
}

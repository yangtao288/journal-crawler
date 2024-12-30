package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalIssueRule;

import java.util.List;

public interface JournalIssueRuleService extends IService<JournalIssueRule> {
    List<JournalIssueRule> findByBasicId(Long basicId);
}

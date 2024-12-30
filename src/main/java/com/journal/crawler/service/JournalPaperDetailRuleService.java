package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalPaperDetailRule;

import java.util.List;

public interface JournalPaperDetailRuleService extends IService<JournalPaperDetailRule> {

    List<JournalPaperDetailRule> findByBasicId(Long basicId);
}
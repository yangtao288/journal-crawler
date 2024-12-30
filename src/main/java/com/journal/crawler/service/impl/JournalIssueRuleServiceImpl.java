package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalIssueRule;
import com.journal.crawler.mapper.JournalIssueRuleMapper;
import com.journal.crawler.service.JournalIssueRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalIssueRuleServiceImpl
        extends ServiceImpl<JournalIssueRuleMapper, JournalIssueRule> implements JournalIssueRuleService {
    @Override
    public List<JournalIssueRule> findByBasicId(Long basicId) {
        LambdaQueryWrapper<JournalIssueRule> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JournalIssueRule::getBasicId, basicId);
        return list(wrapper);
    }
}
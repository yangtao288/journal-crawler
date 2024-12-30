package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalPaperDetailRule;
import com.journal.crawler.mapper.JournalPaperDetailRuleMapper;
import com.journal.crawler.service.JournalPaperDetailRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalPaperDetailRuleServiceImpl
        extends ServiceImpl<JournalPaperDetailRuleMapper, JournalPaperDetailRule> implements JournalPaperDetailRuleService {
    @Override
    public List<JournalPaperDetailRule> findByBasicId(Long basicId) {
        LambdaQueryWrapper<JournalPaperDetailRule> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JournalPaperDetailRule::getBasicId, basicId);
        return list(wrapper);
    }
}
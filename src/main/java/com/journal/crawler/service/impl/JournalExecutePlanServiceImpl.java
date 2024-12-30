package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalExecutePlan;
import com.journal.crawler.mapper.JournalExecutePlanMapper;
import com.journal.crawler.service.JournalExecutePlanService;
import org.springframework.stereotype.Service;

@Service
public class JournalExecutePlanServiceImpl extends
        ServiceImpl<JournalExecutePlanMapper, JournalExecutePlan> implements JournalExecutePlanService {}

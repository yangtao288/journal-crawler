package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalPaperAgency;
import com.journal.crawler.mapper.JournalPaperAgencyMapper;
import com.journal.crawler.service.JournalPaperAgencyService;
import org.springframework.stereotype.Service;

@Service
public class JournalPaperAgencyServiceImpl extends
        ServiceImpl<JournalPaperAgencyMapper, JournalPaperAgency> implements JournalPaperAgencyService {}

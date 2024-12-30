package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalPaperAuthor;
import com.journal.crawler.mapper.JournalPaperAuthorMapper;
import com.journal.crawler.service.JournalPaperAuthorService;
import org.springframework.stereotype.Service;

@Service
public class JournalPaperAuthorServiceImpl extends
        ServiceImpl<JournalPaperAuthorMapper, JournalPaperAuthor> implements JournalPaperAuthorService {}

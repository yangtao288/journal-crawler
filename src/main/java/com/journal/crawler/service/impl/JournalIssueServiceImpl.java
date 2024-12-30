package com.journal.crawler.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalIssue;
import com.journal.crawler.mapper.JournalIssueMapper;
import com.journal.crawler.service.JournalIssueService;
import org.springframework.stereotype.Service;

@Service
public class JournalIssueServiceImpl extends
        ServiceImpl<JournalIssueMapper, JournalIssue> implements JournalIssueService {}

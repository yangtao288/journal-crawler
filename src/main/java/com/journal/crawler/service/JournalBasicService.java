package com.journal.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.crawler.entity.JournalBasic;

public interface JournalBasicService extends IService<JournalBasic> {
    JournalBasic findByBasicId(Long basicId);
}
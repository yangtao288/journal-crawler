package com.journal.crawler.task.strategy.common;

import com.journal.crawler.entity.JournalPaperAgency;
import com.journal.crawler.entity.JournalPaperAuthor;
import com.journal.crawler.entity.JournalPaperDetail;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PaperAuthorCleanStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DefaultPaperAuthorCleanStrategy implements PaperAuthorCleanStrategy {
    @Override
    public BasicIdKeyEnum basicIdKey() {
        return BasicIdKeyEnum.DEFAULT;
    }

    @Override
    public String paperUrlPrefix() {
        return null;
    }

    @Override
    public Map<String, String> expression() {
        return null;
    }

    @Override
    public List<JournalPaperAuthor> cleanAuthors(JournalPaperDetail paperDetail) {
        return null;
    }

    @Override
    public List<JournalPaperAgency> cleanAgencies(JournalPaperDetail paperDetail) {
        return null;
    }
}

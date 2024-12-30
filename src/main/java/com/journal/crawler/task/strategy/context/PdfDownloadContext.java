package com.journal.crawler.task.strategy.context;

import com.journal.crawler.entity.JournalPaperDetail;
import lombok.Data;

@Data
public class PdfDownloadContext {
    private String pdfDownloadXpathRule;
    private String paperUrl;
    private JournalPaperDetail detail;
}
package com.journal.crawler.task.strategy;

import com.journal.crawler.task.strategy.context.PdfDownloadContext;

/**
 * PDF下载策略
 */
public interface PdfDownloadStrategy {

    BasicIdKeyEnum basicIdKey();

    /**
     * 论文链接前缀，识别一本期刊多种论文链接规则
     * @return
     */
    default String paperUrlPrefix() {
        return null;
    }

    boolean downloadPdf(PdfDownloadContext context);
}
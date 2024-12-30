package com.journal.crawler.task.strategy.custom;

import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PdfDownloadStrategy;
import com.journal.crawler.task.strategy.context.PdfDownloadContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 环境工程学报
 *  PDF下载策略
 */
@Slf4j
@Service
public class HjgcxbNewPdfDownloadStrategy implements PdfDownloadStrategy {
    @Override
    public BasicIdKeyEnum basicIdKey() {
        return BasicIdKeyEnum.HJGCXB;
    }

    @Override
    public String paperUrlPrefix() {
        return "http://www.cjee.ac.cn/article/doi/";
    }

    @Override
    public boolean downloadPdf(PdfDownloadContext context) {
        return false;
    }
}

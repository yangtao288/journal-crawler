package com.journal.crawler.task.strategy.common;

import cn.hutool.core.io.FileUtil;
import com.journal.crawler.entity.JournalBasic;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.entity.JournalPaperDetail;
import com.journal.crawler.entity.JournalPaperDetailRule;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PaperDetailCrawlerStrategy;
import com.journal.crawler.utils.JournalParseUtils;
import com.journal.crawler.utils.WebDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import static com.journal.crawler.utils.JournalParseUtils.sanitizedFileName;

@Slf4j
@Service
public class DefaultPaperDetailCrawlerStrategy implements PaperDetailCrawlerStrategy {
    @Value("${webDriver.path}")
    private String driverPath;
    @Value("${webDriver.chromePath}")
    private String chromePath;

    @Value("${fileStore.path}")
    private String fileStorePath;

    @Override
    public BasicIdKeyEnum basicIdKey() {
        return BasicIdKeyEnum.DEFAULT;
    }

    @Override
    public String paperUrlPrefix() {
        return null;
    }

    @Override
    public JournalPaperDetail crawlPaperDetail(JournalBasic basic, JournalPaperDetailRule rule, JournalPaper paper) {
        if (!paper.getPaperUrl().contains(rule.getPaperUrlPrefix())) {
            log.error("论文详情抓取入参有错误，期刊Id：{} 论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        }

        JournalPaperDetail paperDetail = null;
        // 根据页面类型选用不同加载页面方式
        if (rule.getStaticPage()) {
            try {
                paperDetail = crawlerWithJsoup(basic, paper, rule);
            } catch (IOException e) {
                log.error("论文详情抓取入参有错误，期刊Id：{} 论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
                throw new RuntimeException(e);
            }
        } else {
            paperDetail = crawlerWithChromeDriver(basic, paper, rule);
        }

        if (Objects.isNull(paperDetail)) {
            log.error("论文详情抓取入参有错误，期刊Id：{} 论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        }

        return paperDetail;
    }

    private JournalPaperDetail crawlerWithChromeDriver(JournalBasic basic, JournalPaper paper, JournalPaperDetailRule rule) {
        ChromeDriver chromeDriver = WebDriverUtils.getChromeDriver(driverPath, chromePath);
        JournalPaperDetail journalPaperDetail = new JournalPaperDetail();
        String pageHtml = null;
        try {
            pageHtml = JournalParseUtils.parseSJPMHTML(paper.getPaperUrl(), chromeDriver);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chromeDriver抓取论文详情数据出错，期刊Id：{} " +
                    "论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        } finally {
            chromeDriver.quit();
        }

        if (!StringUtils.hasText(pageHtml)) {
            log.error("chromeDriver抓取论文详情html源代码出错，期刊Id：{} " +
                    "论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        }

        // 存储页面源代码（转成静态数据）
        try {
            String dirPath = fileStorePath + File.separator +
                    basic.getJournalName() + File.separator + paper.getIssueId() + File.separator;
            String filePath = dirPath + sanitizedFileName(paper.getTitle()) + ".html";
            FileUtil.writeString(pageHtml, filePath, "UTF-8");
            journalPaperDetail.setHtmlStorePath(FileUtil.getAbsolutePath(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chromeDriver抓取论文详情html文件写入本地失败，期刊Id：{} " +
                    "论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        }

        // 构建论文详情数据
        journalPaperDetail.setPaperId(paper.getId());
        journalPaperDetail.setCreated(new Date());
        journalPaperDetail.setUpdated(new Date());
        try {
            Document document = Jsoup.parse(pageHtml);
            journalPaperDetail.setTitle(JournalParseUtils.getTextWithXpath(document, rule.getTitleRule()));
            journalPaperDetail.setSummary(JournalParseUtils.getTextWithXpath(document, rule.getAbstractRule()));
            journalPaperDetail.setKeywords(JournalParseUtils.getTextWithXpath(document, rule.getKeywordsRule()));
            journalPaperDetail.setAuthorNames(JournalParseUtils.getTextWithXpath(document, rule.getAuthorRule()));
            journalPaperDetail.setAffiliationsName(JournalParseUtils.getTextWithXpath(document, rule.getDeptRule()));
            journalPaperDetail.setClcs(JournalParseUtils.getTextWithXpath(document, rule.getClcsRule()));
            journalPaperDetail.setDoi(JournalParseUtils.getTextWithXpath(document, rule.getDoiRule()));
            journalPaperDetail.setFund(JournalParseUtils.getTextWithXpath(document, rule.getFundRule()));

            paper.setTitle(journalPaperDetail.getTitle());
            paper.setPeriod(JournalParseUtils.getTextWithXpath(document, rule.getIssueRule()));
            paper.setUpdated(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chromeDriver抓取论文详情后Jsoup解析论文html出现异常，期刊Id：{} " +
                    "论文地址前缀：{} 论文地址：{}", rule.getBasicId(), rule.getPaperUrlPrefix(), paper.getPaperUrl());
            return null;
        }

        return journalPaperDetail;
    }

    private JournalPaperDetail crawlerWithJsoup(JournalBasic basic, JournalPaper paper, JournalPaperDetailRule rule) throws IOException {
        Document document = Jsoup.parse(new URL(JournalParseUtils.encodeChineseOnly(paper.getPaperUrl())), 5000);

        JournalPaperDetail journalPaperDetail = new JournalPaperDetail();
        journalPaperDetail.setPaperId(paper.getId());
        journalPaperDetail.setTitle(JournalParseUtils.getTextWithXpath(document, rule.getTitleRule()));
        journalPaperDetail.setSummary(JournalParseUtils.getTextWithXpath(document, rule.getAbstractRule()));
        journalPaperDetail.setKeywords(JournalParseUtils.getTextWithXpath(document, rule.getKeywordsRule()));
        journalPaperDetail.setAuthorNames(JournalParseUtils.getTextWithXpath(document, rule.getAuthorRule()));
        journalPaperDetail.setAffiliationsName(JournalParseUtils.getTextWithXpath(document, rule.getDeptRule()));
        journalPaperDetail.setClcs(JournalParseUtils.getTextWithXpath(document, rule.getClcsRule()));
        journalPaperDetail.setDoi(JournalParseUtils.getTextWithXpath(document, rule.getDoiRule()));
        journalPaperDetail.setFund(JournalParseUtils.getTextWithXpath(document, rule.getFundRule()));
        journalPaperDetail.setCreated(new Date());
        journalPaperDetail.setUpdated(new Date());

        paper.setTitle(journalPaperDetail.getTitle());
        paper.setPeriod(JournalParseUtils.getTextWithXpath(document, rule.getIssueRule()));
        paper.setUpdated(new Date());

        // 存储页面源代码
        String pageHtml = JournalParseUtils.parseStaticHTML(document);
        String dirPath = fileStorePath + File.separator +
                basic.getJournalName() + File.separator + paper.getIssueId() + File.separator;
        String filePath = dirPath + sanitizedFileName(journalPaperDetail.getTitle()) + ".html";
        FileUtil.writeString(pageHtml, filePath, "UTF-8");
        journalPaperDetail.setHtmlStorePath(FileUtil.getAbsolutePath(filePath));

        return journalPaperDetail;
    }
}
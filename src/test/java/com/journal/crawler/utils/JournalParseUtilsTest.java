package com.journal.crawler.utils;

import cn.hutool.core.io.FileUtil;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.entity.JournalPaperDetail;
import com.journal.crawler.entity.JournalPaperDetailRule;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.Date;

class JournalParseUtilsTest {
    String chromePath = "D:\\tools-periodical-parser\\chrome-win64\\chrome.exe";
    String driverPath = "D:\\tools-periodical-parser\\chromedriver-win64\\chromedriver.exe";
    String fileStorePath = "D:\\store-journal-crawler";

    JournalPaper paper = new JournalPaper();

    JournalPaperDetailRule rule = new JournalPaperDetailRule();

    @BeforeEach
    void setUp() {
        paper.setBasicId(1020123L);
        paper.setPaperUrl("http://www.cjee.ac.cn/article/doi/10.12030/j.cjee.202309125");
        paper.setIssueId(10002L);

        rule.setId(10002L);
        rule.setBasicId(1020123L);
        rule.setPaperUrlPrefix("http://www.cjee.ac.cn/article/doi/");
        rule.setPaperUrl("http://www.cjee.ac.cn/article/doi/10.12030/j.cjee.202311123");
        rule.setStaticPage(false);
        rule.setTitleRule("//section[@class=\"articleCn\"]/h2[1]");
        rule.setAbstractRule("//section[@class=\"articleCn\"]/div[2]/p[1]");
        rule.setKeywordsRule("//section[@class=\"articleCn\"]/div[2]/ul[1]");
        rule.setFundRule("//div[@class=\"articleCn\"]/ul[2]/li[4]/div[1]/div[1]");
        rule.setAuthorRule("//section[@class=\"articleCn\"]/ul[1]");
        rule.setDeptRule("//div[@class=\"articleCn\"]/ul[2]/li");
        rule.setDoiRule("无");
        rule.setClcsRule("//p[@class=\"com-author-info\"]");
        rule.setPdfRule("//div[@class=\"in-bl\"]/span[1]");
        rule.setIssueRule("//div[@class=\"inner content container\"]/div[2]/div[1]/table[1]/tbody[1]/tr[1]/td[2]/span[1]");
    }

    @Test
    public void testXpathWithChromeDriver() {
        ChromeDriver chromeDriver = WebDriverUtils.getChromeDriver(driverPath, chromePath);
        try {
            String pageHtml = JournalParseUtils.parseSJPMHTML(paper.getPaperUrl(), chromeDriver);

            JournalPaperDetail journalPaperDetail = new JournalPaperDetail();
            journalPaperDetail.setPaperId(paper.getId());
            journalPaperDetail.setTitle(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getTitleRule()));
            journalPaperDetail.setSummary(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getAbstractRule()));
            journalPaperDetail.setKeywords(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getKeywordsRule()));
            journalPaperDetail.setAuthorNames(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getAuthorRule()));
            journalPaperDetail.setAffiliationsName(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getDeptRule()));
            journalPaperDetail.setClcs(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getClcsRule()));
            journalPaperDetail.setDoi(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getDoiRule()));
            journalPaperDetail.setFund(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getFundRule()));
            journalPaperDetail.setCreated(new Date());
            journalPaperDetail.setUpdated(new Date());

            paper.setTitle(journalPaperDetail.getTitle());
            paper.setPeriod(JournalParseUtils.getTextWithXpath(chromeDriver, rule.getIssueRule()));
            paper.setUpdated(new Date());

            // 存储页面源代码（转成静态数据）
            String dirPath = fileStorePath + File.separator +
                    "环境工程学报" + File.separator + paper.getIssueId() + File.separator;
            String filePath = dirPath + journalPaperDetail.getTitle() + ".html";
            FileUtil.writeString(pageHtml, filePath, "UTF-8");
            journalPaperDetail.setHtmlStorePath(FileUtil.getAbsolutePath(filePath));

            System.out.println(journalPaperDetail);
            System.out.println(paper);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chromeDriver.quit();
        }
    }

    @Test
    public void testXpathWithJsoup() {
        // 生成文件到本地后 读取本地文件
        Document document = JournalParseUtils.parseLocalHtmlFile("D:\\store-journal-crawler\\环境工程学报\\10002\\连续流好氧颗粒污泥强化策略研究进展.html");

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

        System.out.println(journalPaperDetail);
        System.out.println(paper);
    }
}
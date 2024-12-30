package com.journal.crawler.task.strategy.common;

import com.google.common.collect.Lists;
import com.journal.crawler.entity.JournalIssueRule;
import com.journal.crawler.entity.JournalPaper;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PaperLinkCrawlerStrategy;
import com.journal.crawler.utils.JournalParseUtils;
import com.journal.crawler.utils.WebDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DefaultPaperLinkCrawlerStrategy implements PaperLinkCrawlerStrategy {
    @Value("${webDriver.path}")
    private String driverPath;
    @Value("${webDriver.chromePath}")
    private String chromePath;

    @Override
    public BasicIdKeyEnum basicIdKey() {
        return BasicIdKeyEnum.DEFAULT;
    }

    @Override
    public List<JournalPaper> crawlPaperLinks(JournalIssueRule rule, String issueUrl) {
        if (!issueUrl.contains(rule.getIssueUrlPrefix())) {
            log.error("论文链接抓取入参有错误，期刊Id：{} 规则前缀：{} 当前卷期地址：{}", rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
            return null;
        }

        List<JournalPaper> papers = Lists.newArrayList();
        // 根据页面类型选用不同加载页面方式
        if (rule.getStaticPage()) {
            try {
                papers = crawlerWithJsoup(issueUrl, rule);
            } catch (IOException e) {
                log.error("静态页面加载异常，期刊Id：{} 规则前缀：{} 当前卷期地址：{}", rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
                throw new RuntimeException(e);
            }
        } else {
            papers = crawlerWithChromeDriver(issueUrl, rule);
        }

        if (CollectionUtils.isEmpty(papers)) {
            log.error("论文链接抓取数据为空，期刊Id：{} 规则前缀：{} 当前卷期地址：{}", rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
            return null;
        }

        return papers;
    }

    private List<JournalPaper> crawlerWithJsoup(String issueUrl, JournalIssueRule rule) throws IOException {
        List<JournalPaper> papers = Lists.newArrayList();
        Document document = Jsoup.parse(new URL(issueUrl), 5000);
        if (StringUtils.hasText(rule.getClassStyleRule())) {
            Elements elements = document.getElementsByClass(rule.getClassStyleRule());
            if (Objects.isNull(elements)) {
                log.error("论文链接抓取异常，类样式:{} 期刊Id：{} 规则前缀：{} 当前卷期地址：{}",
                        rule.getClassStyleRule(), rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
                return null;
            }
            for (Element element : elements) {
                String link = element.attr("href");
                String title = element.text();
                JournalPaper journalPaper = new JournalPaper();
                journalPaper.setBasicId(rule.getBasicId());
                journalPaper.setPaperUrl(link);
                journalPaper.setTitle(title);
                papers.add(journalPaper);
            }
        } else if (StringUtils.hasText(rule.getXPathRule())) {
            Elements elements = document.selectXpath(rule.getXPathRule());
            if (Objects.isNull(elements)) {
                log.error("论文链接抓取异常，xPath:{} 期刊Id：{} 规则前缀：{} 当前卷期地址：{}",
                        rule.getXPathRule(), rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
                return null;
            }
            for (Element element : elements) {
                String link = element.attr("href");
                String title = element.text();
                JournalPaper journalPaper = new JournalPaper();
                journalPaper.setBasicId(rule.getBasicId());
                journalPaper.setPaperUrl(link);
                journalPaper.setTitle(title);
                papers.add(journalPaper);
            }
        } else if (StringUtils.hasText(rule.getScript())) {
            // 处理定制化脚本，不在这里处理
            // TODO do nothing
        }
        return papers;
    }

    private List<JournalPaper> crawlerWithChromeDriver(String issueUrl, JournalIssueRule rule) {
        List<JournalPaper> papers = Lists.newArrayList();
        ChromeDriver chromeDriver = WebDriverUtils.getChromeDriver(driverPath, chromePath);
        try {
            JournalParseUtils.parseSJPMHTML(issueUrl, chromeDriver);

            if (StringUtils.hasText(rule.getClassStyleRule())) {
                List<WebElement> elements = chromeDriver.findElements(By.className(rule.getClassStyleRule()));
                if (Objects.isNull(elements)) {
                    log.error("论文链接抓取异常，类样式:{} 期刊Id：{} 规则前缀：{} 当前卷期地址：{}",
                            rule.getClassStyleRule(), rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
                    return null;
                }
                for (WebElement element : elements) {
                    String link = element.getAttribute("href");
                    String title = element.getText();
                    JournalPaper journalPaper = new JournalPaper();
                    journalPaper.setBasicId(rule.getBasicId());
                    journalPaper.setPaperUrl(link);
                    journalPaper.setTitle(title);
                    papers.add(journalPaper);
                }
            } else if (StringUtils.hasText(rule.getXPathRule())) {
                List<WebElement> elements = chromeDriver.findElements(By.xpath(rule.getXPathRule()));
                if (Objects.isNull(elements)) {
                    log.error("论文链接抓取异常，xPath:{} 期刊Id：{} 规则前缀：{} 当前卷期地址：{}",
                            rule.getXPathRule(), rule.getBasicId(), rule.getIssueUrlPrefix(), issueUrl);
                    return null;
                }
                for (WebElement element : elements) {
                    String link = element.getAttribute("href");
                    String title = element.getText();
                    JournalPaper journalPaper = new JournalPaper();
                    journalPaper.setBasicId(rule.getBasicId());
                    journalPaper.setPaperUrl(link);
                    journalPaper.setTitle(title);
                    papers.add(journalPaper);
                }
            } else if (StringUtils.hasText(rule.getScript())) {
                // 处理定制化脚本，不在这里处理
                // TODO do nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chromeDriver论文链接抓取异常，规则前缀：{} 当前卷期地址：{}", rule.getIssueUrlPrefix(), issueUrl);
            return null;
        } finally {
            chromeDriver.quit();
        }
        return papers;
    }
}
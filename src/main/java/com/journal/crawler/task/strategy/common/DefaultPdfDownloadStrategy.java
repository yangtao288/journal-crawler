package com.journal.crawler.task.strategy.common;

import com.journal.crawler.entity.JournalPaperDetail;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PdfDownloadStrategy;
import com.journal.crawler.task.strategy.context.PdfDownloadContext;
import com.journal.crawler.utils.DownloadPDFUtils;
import com.journal.crawler.utils.JournalParseUtils;
import com.journal.crawler.utils.WebDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.journal.crawler.utils.JournalParseUtils.sanitizedFileName;

@Slf4j
@Service
public class DefaultPdfDownloadStrategy implements PdfDownloadStrategy {
    @Value("${webDriver.path}")
    private String driverPath;
    @Value("${webDriver.chromePath}")
    private String chromePath;

    @Override
    public BasicIdKeyEnum basicIdKey() {
        return BasicIdKeyEnum.DEFAULT;
    }

    @Override
    public boolean downloadPdf(PdfDownloadContext context) {
        String htmlStorePath = context.getDetail().getHtmlStorePath();
        if (!StringUtils.hasText(htmlStorePath)) {
            return false;
        }

        // 从保存的静态页面中获取 meta citation_pdf_url值
        Boolean returnValue = directDownload(htmlStorePath, context.getPaperUrl(), context.getDetail());
        if (Objects.nonNull(returnValue)) {
            return returnValue;
        }

        // 有配置xpath下载规则
        final String basePath = htmlStorePath.replace(
                sanitizedFileName(context.getDetail().getTitle()), "").replace(".html", "");
        return chromeDriverDownload(context.getPdfDownloadXpathRule(), context.getPaperUrl(), basePath, context.getDetail());
    }

    private Boolean directDownload(String htmlStorePath, String paperUrl, JournalPaperDetail detail) {
        final String basePath = htmlStorePath.replace(
                sanitizedFileName(detail.getTitle()), "").replace(".html", "");
        final String fileName = sanitizedFileName(detail.getTitle());
        Document document = JournalParseUtils.parseLocalHtmlFile(htmlStorePath);
        if (Objects.nonNull(document)) {
            Elements metas = document.getElementsByTag("meta");
            String realDownloadUrl = null;
            for (Element element : metas) {
                String name = element.attr("name");
                String content = element.attr("content");
                if ("citation_pdf_url".equals(name)) {
                    log.info("pdf下载请求url:{}", content);
                    realDownloadUrl = content;
                    break;
                }
            }

            // 论文页地址和下载地址不一样情况，才可以考虑下载
            if (StringUtils.hasText(realDownloadUrl) && !paperUrl.equals(realDownloadUrl)) {
                boolean success = JournalParseUtils.downloadPdf(realDownloadUrl, basePath, fileName);
                if (success) {
                    detail.setPdfUrl(realDownloadUrl);
                    detail.setPdfStorePath(basePath + fileName + ".pdf");
                }
                return success;
            }
        }
        return null;
    }

    private boolean chromeDriverDownload(
            String pdfDownloadXpathRule, String paperUrl, String basePath, JournalPaperDetail detail) {
        // 有配置xpath下载规则
        if (StringUtils.hasText(pdfDownloadXpathRule) && !"无".equals(pdfDownloadXpathRule)) {
            ChromeDriver driver = WebDriverUtils.builder(basePath, driverPath, chromePath);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            try {
                // 设置隐式等待
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                // 设置访问地址
                driver.get(paperUrl);
                // 等待页面完全加载
                wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                WebElement pdfElement = driver.findElementByXPath(pdfDownloadXpathRule);
                try {
                    // 滚动到元素位置
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pdfElement);
                    // 等待元素可点击
                    wait.until(ExpectedConditions.elementToBeClickable(pdfElement));
                    // 尝试点击
                    pdfElement.click();
                } catch (ElementNotInteractableException e) {
                    // 如果常规点击失败，尝试使用JavaScript点击
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    executor.executeScript("arguments[0].click();", pdfElement);
                }
                String fullPath = DownloadPDFUtils.waitForDownload(basePath, 60);
                if (StringUtils.hasText(fullPath)) {
                    detail.setPdfStorePath(fullPath);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        DefaultPdfDownloadStrategy strategy = new DefaultPdfDownloadStrategy();
        strategy.driverPath = "D:\\tools-periodical-parser\\chromedriver-win64\\chromedriver.exe";
        strategy.chromePath = "D:\\tools-periodical-parser\\chrome-win64\\chrome.exe";

        String pdfDownloadXpathRule = "//button[@class=\"btn pdf-btn download-pdf\"]";
        String paperUrl = "http://www.cjee.ac.cn/article/doi/10.12030/j.cjee.202311088";
        JournalPaperDetail detail = new JournalPaperDetail();
        detail.setTitle("碳点复合材料光催化降解腐殖酸的机制");
        detail.setHtmlStorePath("D:/store-journal-crawler/环境工程学报/10007/碳点复合材料光催化降解腐殖酸的机制.html");
        PdfDownloadContext context = new PdfDownloadContext();
        context.setDetail(detail);
        context.setPdfDownloadXpathRule(pdfDownloadXpathRule);
        context.setPaperUrl(paperUrl);
        strategy.downloadPdf(context);
    }
}

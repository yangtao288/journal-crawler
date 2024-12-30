package com.journal.crawler.utils;

import com.google.common.collect.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JournalParseUtils {
    public static String getTextWithXpath(Document document, String xpath) {
        if ("无".equals(xpath)) {
            return "";
        }
        Elements elements = document.selectXpath(xpath);
        if (elements.size() > 0) {
            return elements.text();
        }
        return "";
    }

    public static String getTextWithXpath(ChromeDriver chromeDriver, String xpath) {
        if ("无".equals(xpath)) {
            return "";
        }
        try {
            List<WebElement> elementsByXPath = chromeDriver.findElementsByXPath(xpath);
            if (!CollectionUtils.isEmpty(elementsByXPath)) {
                List<String> contents = Lists.newArrayList();
                for (WebElement element : elementsByXPath) {
                    contents.add(element.getText());
                }
                return String.join(" ", contents);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }

    public static String encodeChineseOnly(String url) {
        // 使用StringBuilder构建最终的URL
        StringBuilder encodedUrl = new StringBuilder();
        // 将URL字符串转换为字符数组
        char[] chars = url.toCharArray();

        for (char c : chars) {
            // 检查字符是否为中文
            if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]")) {
                // 对中文字符进行URL编码
                try {
                    encodedUrl.append(URLEncoder.encode(String.valueOf(c), "GB2312"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 非中文字符直接添加到结果中
                encodedUrl.append(c);
            }
        }
        return encodedUrl.toString();
    }

    public static String parseStaticHTML(Document document) {
        String result = "";
        try {
            if (Objects.isNull(document)) {
                return result;
            }
            result = document.html();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Document parseLocalHtmlFile(String htmlFilePath) {
        try {
            File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            return doc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseSJPMHTML(String paperUrl, ChromeDriver chromeDriver) {
        String result = "";
        try {
            Thread.sleep(1500);
            // 设置访问地址
            paperUrl = encodeChineseOnly(paperUrl);
            chromeDriver.get(paperUrl);

            // 设置等待2s
            chromeDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            result = chromeDriver.getPageSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean downloadPdf(String url, String basePath, String fileName) {
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            // 设置连接超时时间（单位：毫秒）
            conn.setConnectTimeout(20000); // 20秒
            // 设置读取超时时间（单位：毫秒）
            conn.setReadTimeout(20000); // 20秒
            // 设置请求方法
            conn.setRequestMethod("GET");
            // 添加请求头
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(basePath + fileName +  ".pdf");
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 读取数据并写入文件
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String sanitizedFileName(String fileName) {
        return fileName.replace("/", "_").replace("\\", "_");
    }
}

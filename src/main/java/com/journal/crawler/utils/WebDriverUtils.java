package com.journal.crawler.utils;

import com.google.common.collect.Lists;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebDriverUtils {

    private static ChromeOptions baseChromeOptions(String chromePath) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--ignore-certificate-errors"); // 忽略SSL证书错误
        chromeOptions.addArguments("--disable-images");
        chromeOptions.addArguments("--blink-settings=imagesEnabled=false");
        // 禁用所有扩展
        chromeOptions.addArguments("--disable-extensions");
        // 可以选择其他选项，比如禁用自动更新
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.setBinary(chromePath);
        return chromeOptions;
    }

    public static ChromeDriver getChromeDriver(String driverPath, String chromePath) {
        System.getProperties().setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver(baseChromeOptions(chromePath));
    }

    public static ChromeDriver builder(String downloadPath, String driverPath, String chromePath) {
        // 加载谷歌浏览器驱动
        System.getProperties().setProperty("webdriver.chrome.driver", driverPath);
        ChromeOptions options = baseChromeOptions(chromePath);
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.default_directory", downloadPath); // 设置下载目录
        prefs.put("download.prompt_for_download", false); // 不提示下载
        prefs.put("download.directory_upgrade", true);
        prefs.put("plugins.always_open_pdf_externally", true); // 直接下载 PDF 文件，而不是在浏览器中打开
        List<String> enabledAuto = Lists.newArrayList();
        enabledAuto.add("enable-automation");
        prefs.put("excludeSwitches", enabledAuto);
        prefs.put("useAutomationExtension", false);
        prefs.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.360");
        options.setExperimentalOption("prefs", prefs);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        return chromeDriver;
    }
}
package com.journal.crawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.journal.crawler.mapper")
public class JournalCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JournalCrawlerApplication.class, args);
    }
}
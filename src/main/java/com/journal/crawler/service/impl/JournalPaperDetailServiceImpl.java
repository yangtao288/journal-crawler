package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.journal.crawler.entity.*;
import com.journal.crawler.enums.GrabStatusEnum;
import com.journal.crawler.enums.JournalExecuteHistoryPhaseEnum;
import com.journal.crawler.mapper.JournalPaperDetailMapper;
import com.journal.crawler.service.JournalExecuteHistoryService;
import com.journal.crawler.service.JournalPaperDetailRuleService;
import com.journal.crawler.service.JournalPaperDetailService;
import com.journal.crawler.service.JournalPaperService;
import com.journal.crawler.task.strategy.BasicIdKeyEnum;
import com.journal.crawler.task.strategy.PaperDetailCrawlerStrategy;
import com.journal.crawler.task.strategy.PdfDownloadStrategy;
import com.journal.crawler.task.strategy.context.PdfDownloadContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class JournalPaperDetailServiceImpl extends
        ServiceImpl<JournalPaperDetailMapper, JournalPaperDetail> implements JournalPaperDetailService {

    @Autowired
    private JournalPaperDetailRuleService paperDetailRuleService;

    @Autowired
    private JournalExecuteHistoryService executeHistoryService;

    @Autowired
    private JournalPaperService paperService;

    @Autowired
    private List<PaperDetailCrawlerStrategy> paperDetailCrawlerStrategies;

    private Map<BasicIdKeyEnum, PaperDetailCrawlerStrategy> paperDetailCrawlerStrategyMap;

    @Autowired
    private List<PdfDownloadStrategy> pdfDownloadStrategies;

    private Map<BasicIdKeyEnum, PdfDownloadStrategy> pdfDownloadStrategyMap;

    @PostConstruct
    public void init() {
        paperDetailCrawlerStrategyMap = Maps.newHashMap();
        for (PaperDetailCrawlerStrategy paperDetailCrawlerStrategy : paperDetailCrawlerStrategies) {
            paperDetailCrawlerStrategyMap.put(paperDetailCrawlerStrategy.basicIdKey(), paperDetailCrawlerStrategy);
        }

        pdfDownloadStrategyMap = Maps.newHashMap();
        for (PdfDownloadStrategy pdfDownloadStrategy : pdfDownloadStrategies) {
            pdfDownloadStrategyMap.put(pdfDownloadStrategy.basicIdKey(), pdfDownloadStrategy);
        }
    }

    public List<JournalPaperDetail> paperDetailsCrawler(JournalBasic basic, JournalExecutePlan plan, List<JournalPaper> papers) {
        BasicIdKeyEnum basicIdKeyEnum = BasicIdKeyEnum.convert(plan.getBasicId());
        List<JournalPaperDetailRule> paperDetailRules = paperDetailRuleService.findByBasicId(plan.getBasicId());
        if (CollectionUtils.isEmpty(paperDetailRules)) {
            return null;
        }

        // 按paperUrlPrefix长度倒序排序
        paperDetailRules.sort(Comparator.comparingInt(a -> a.getPaperUrlPrefix().length()));
        List<JournalPaperDetail> paperDetails = Lists.newArrayList();
        for (JournalPaper journalPaper : papers) {
            JournalPaperDetailRule onlyOneDetailRule = paperDetailRules.stream().filter(
                    s -> journalPaper.getPaperUrl().contains(s.getPaperUrlPrefix())).findFirst().get();
            JournalPaperDetail journalPaperDetail = paperDetailCrawlerStrategyMap.get(
                    basicIdKeyEnum).crawlPaperDetail(basic, onlyOneDetailRule, journalPaper);
            if (Objects.nonNull(journalPaperDetail)) {
                journalPaper.setGrabStatus(GrabStatusEnum.SUCCESS.name());
                journalPaper.setUpdated(new Date());
                paperDetails.add(journalPaperDetail);
            } else {
                journalPaper.setGrabStatus(GrabStatusEnum.FAILED.name());
                journalPaper.setUpdated(new Date());
            }

            try {
                Thread.sleep(2000); // 延后2s再进行
            } catch (InterruptedException e) {
                log.error("延后2s出现中断异常，e：{}", e.getMessage());
            }
        }

        // 批量更新
        paperService.updateBatchById(papers);
        if (!CollectionUtils.isEmpty(paperDetails)) {
            saveBatch(paperDetails);
        }

        // 记录抓取论文详情数据历史
        executeHistoryService.saveJournalExecuteHistoryRecord(plan,
                JournalExecuteHistoryPhaseEnum.DETAILS.name(), paperDetails.size(), papers.size() - paperDetails.size());
        return paperDetails;
    }

    @Override
    public void paperPdfDownload(JournalBasic basic, JournalExecutePlan plan, List<JournalPaperDetail> details) {
        int successNum = 0;
        int failedNum = 0;
        for (JournalPaperDetail detail : details) {
            boolean success = paperPdfDownload(basic, plan, detail);
            if (success) {
                successNum ++;
            } else {
                failedNum ++;
            }
        }

        // 记录下载论文数据历史
        executeHistoryService.saveJournalExecuteHistoryRecord(plan,
                JournalExecuteHistoryPhaseEnum.PDF_DOWNLOAD.name(), successNum, failedNum);
    }

    private boolean paperPdfDownload(JournalBasic basic, JournalExecutePlan plan, JournalPaperDetail detail) {
        BasicIdKeyEnum basicIdKeyEnum = BasicIdKeyEnum.convert(plan.getBasicId());
        List<JournalPaperDetailRule> paperDetailRules = paperDetailRuleService.findByBasicId(plan.getBasicId());
        if (CollectionUtils.isEmpty(paperDetailRules)) {
            log.error("Pdf download configuration rule is empty, " +
                    "please to checked, basicId: {}, planId:{}, detailId: {}", basic.getId(), plan.getId(), detail.getId());
            return false;
        }

        // 按paperUrlPrefix长度倒序排序
        paperDetailRules.sort(Comparator.comparingInt(a -> a.getPaperUrlPrefix().length()));
        JournalPaper journalPaper = paperService.getById(detail.getPaperId());
        JournalPaperDetailRule onlyOneDetailRule = paperDetailRules.stream().filter(
                s -> journalPaper.getPaperUrl().contains(s.getPaperUrlPrefix())).findFirst().get();
        PdfDownloadContext context = new PdfDownloadContext();
        context.setPaperUrl(journalPaper.getPaperUrl());
        context.setPdfDownloadXpathRule(onlyOneDetailRule.getPdfRule());
        context.setDetail(detail);
        boolean success = pdfDownloadStrategyMap.get(basicIdKeyEnum).downloadPdf(context);
        if (success) {
            detail.setPdfGrabStatus(GrabStatusEnum.SUCCESS.name());
        } else {
            detail.setPdfGrabStatus(GrabStatusEnum.FAILED.name());
        }
        detail.setUpdated(new Date());
        updateById(detail);

        return success;
    }
}

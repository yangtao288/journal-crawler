package com.journal.crawler.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.journal.crawler.entity.*;
import com.journal.crawler.mapper.JournalExecutePlanMapper;
import com.journal.crawler.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JournalCrawlerTask {
    @Autowired
    private JournalExecutePlanMapper planMapper;

    @Autowired
    private JournalPaperService paperService;

    @Autowired
    private JournalPaperDetailService journalPaperDetailService;

    @Autowired
    private JournalBasicService basicService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_PROCESSING_PREFIX = "journal_processing:";

    //@PostConstruct
    public void init() {
        executeCrawlerTask();
    }

    //@Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void executeCrawlerTask() {
        // 获取一条未在Redis中处理的待执行计划
        JournalExecutePlan plan = getUniquePendingPlan();
        if (Objects.nonNull(plan)) {
            try {
                // 执行爬虫任务
                processCrawlerPlan(plan);
            } catch (Exception e) {
                log.error("Crawler task execution failed", e);
                // 异常时重置状态，允许后续重试
                resetPlanStatus(plan);
            }
        }
    }

    private JournalExecutePlan getUniquePendingPlan() {
        // 查询条件：
        // 1. 状态为PENDING
        // 2. basicId不在处理中
        // 3. basicId不在当前实例已处理列表中
        QueryWrapper<JournalExecutePlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exec_status", "PENDING");
        queryWrapper.orderByAsc("created");
        queryWrapper.last("limit 10");
        List<JournalExecutePlan> executePlanList = planMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(executePlanList)) {
            log.warn("Not find plan list with execute status is pending");
            return null;
        }

        JournalExecutePlan onePlan = executePlanList.get(0);
        // 当前机器有正在执行的期刊数据
        String basicId = redisTemplate.opsForValue().get(uniqueInstanceRedisKey());
        if (StringUtils.hasText(basicId)) {
            // 排除当前机器正在执行的期刊
            onePlan = executePlanList.stream().filter(s -> !s.getBasicId().equals(Long.getLong(basicId))).findFirst().get();
        }

        // 标记为处理中
        if (markPlanAsProcessing(onePlan)) {
            // 记录当前实例已处理的任务ID
            recordInstanceTask(onePlan);
            return onePlan;
        }

        return null;
    }

    private boolean markPlanAsProcessing(JournalExecutePlan plan) {
        // 更新状态为处理中
        UpdateWrapper<JournalExecutePlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(JournalExecutePlan::getExecStatus, "PROCESSING")
                .set(JournalExecutePlan::getUpdated, new Date())
                .eq(JournalExecutePlan::getId, plan.getId())
                .eq(JournalExecutePlan::getExecStatus, "PENDING");

        int updatedRows = planMapper.update(null, updateWrapper);
        if (updatedRows > 0) {
            // 在Redis中标记该basicId正在处理
            String processingKey = REDIS_PROCESSING_PREFIX + plan.getBasicId();
            redisTemplate.opsForValue().set(processingKey, "1", 2, TimeUnit.HOURS);
            return true;
        }

        return false;
    }


    private void processCrawlerPlan(JournalExecutePlan plan) {
        try {
            log.info("Crawler plan, basicId: {}, issueUrl: {}", plan.getBasicId(), plan.getIssueUrl());
            JournalBasic journalBasic = basicService.findByBasicId(plan.getBasicId());
            if (Objects.isNull(journalBasic)) {
                log.warn("Crawler plan return " +
                        "no journal data, basicId: {}, issueId: {}", plan.getBasicId(), plan.getIssueId());
                return ;
            }

            // 具体爬虫处理逻辑
            // 1. 抓取论文链接
            List<JournalPaper> journalPapers = paperService.paperLinkCrawler(plan);
            if (CollectionUtils.isEmpty(journalPapers)) {
                log.warn("Crawler paper links return " +
                        "no issue rule, basicId: {}, issueId: {}", plan.getBasicId(), plan.getIssueId());
                return ;
            }

            // 2. 抓取论文详情
            List<JournalPaperDetail> paperDetails = journalPaperDetailService.paperDetailsCrawler(journalBasic, plan, journalPapers);
            if (CollectionUtils.isEmpty(paperDetails)) {
                log.warn("Crawler papers detail return " +
                        "no detail rule, basicId: {}, issueId: {}", plan.getBasicId(), plan.getIssueId());
                return ;
            }

            // 3. PDF下载
            journalPaperDetailService.paperPdfDownload(journalBasic, plan, paperDetails);

            // 4. 清洗数据

            // 处理完成后更新状态
            updatePlanStatusToSuccess(plan);
        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常，可能需要重试
            handlePlanProcessingFailure(plan);
        }
    }


    private void updatePlanStatusToSuccess(JournalExecutePlan plan) {
        // 更新状态为成功
        UpdateWrapper<JournalExecutePlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(JournalExecutePlan::getExecStatus, "SUCCESS")
                .set(JournalExecutePlan::getUpdated, new Date())
                .eq(JournalExecutePlan::getId, plan.getId());

        planMapper.update(null, updateWrapper);

        // 清理Redis标记
        String processingKey = REDIS_PROCESSING_PREFIX + plan.getBasicId();
        redisTemplate.delete(processingKey);
    }

    private void handlePlanProcessingFailure(JournalExecutePlan plan) {
        // 更新状态为失败
        UpdateWrapper<JournalExecutePlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(JournalExecutePlan::getExecStatus, "FAILED")
                .set(JournalExecutePlan::getUpdated, new Date())
                .eq(JournalExecutePlan::getId, plan.getId());

        planMapper.update(null, updateWrapper);

        // 清理Redis标记
        String processingKey = REDIS_PROCESSING_PREFIX + plan.getBasicId();
        redisTemplate.delete(processingKey);

        String instanceKey = uniqueInstanceRedisKey();
        redisTemplate.delete(instanceKey);
    }

    private void resetPlanStatus(JournalExecutePlan plan) {
        // 重置为待处理状态
        UpdateWrapper<JournalExecutePlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(JournalExecutePlan::getExecStatus, "PENDING")
                .set(JournalExecutePlan::getUpdated, new Date())
                .eq(JournalExecutePlan::getId, plan.getId());

        planMapper.update(null, updateWrapper);

        // 清理Redis标记
        String processingKey = REDIS_PROCESSING_PREFIX + plan.getBasicId();
        redisTemplate.delete(processingKey);

        String instanceKey = uniqueInstanceRedisKey();
        redisTemplate.delete(instanceKey);
    }

    private void recordInstanceTask(JournalExecutePlan plan) {
        // 记录当前实例已处理的任务ID
        String redisKey = uniqueInstanceRedisKey();

        redisTemplate.opsForSet().add(redisKey, String.valueOf(plan.getId()));
        // 设置过期时间，防止无限增长
        // 1个期次的数据大概20-30篇论文
        redisTemplate.expire(redisKey, 2, TimeUnit.HOURS);
    }

    private String uniqueInstanceRedisKey() {
        String instanceId = generateUniqueInstanceId();
        return "redis_instance_tasks_" + instanceId;
    }

    private String generateUniqueInstanceId() {
        // 生成实例唯一标识，可以是机器IP、进程ID等
        return ManagementFactory.getRuntimeMXBean().getName();
    }
}
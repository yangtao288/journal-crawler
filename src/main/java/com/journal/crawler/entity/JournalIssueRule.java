package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_issue_rule")
public class JournalIssueRule implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long basicId; // 期刊唯一标识
    private String issueUrlPrefix; // 卷期url前缀，识别同一本期刊下多套卷期规则
    private Boolean staticPage; // 是否静态页面
    private String xPathRule; // xPath解析规则
    private String classStyleRule; // 类样式解析规则
    private String script; // 脚本支持方式
    private Date created;
    private Date updated;
}
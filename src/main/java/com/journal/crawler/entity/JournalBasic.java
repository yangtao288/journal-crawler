package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_basic")
public class JournalBasic implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String journalName;
    private String domainUrl;
    private String currentIssueUrl;
    private String historyIssueListUrl;
    private Boolean isContainCurrent;
    private Date created;
    private Date updated;
}
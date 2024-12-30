package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_issue")
public class JournalIssue implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long basicId;
    private String issueUrl;
    private Integer year;
    private String period;
    private String grabStatus;
    private Integer retryCount;
    private Date created;
    private Date updated;
}
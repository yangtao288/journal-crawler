package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_execute_plan")
public class JournalExecutePlan implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long basicId;
    private Long issueId;
    private String issueUrl;
    private String execStatus;
    private Date created;
    private Date updated;
}
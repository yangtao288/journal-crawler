package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_execute_history")
public class JournalExecuteHistory implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long basicId;
    private Long issueId;
    private Long planId;
    private String issueUrl;
    private String phase;
    private String status;
    private Integer successNum;
    private Integer failedNum;
    private Date created;
    private Date updated;
}

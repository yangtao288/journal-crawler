package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("journal_paper_author")
public class JournalPaperAuthor implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long paperId;
    private String authorName;
    private String affiliationsCodes;
    private Integer sort;
}
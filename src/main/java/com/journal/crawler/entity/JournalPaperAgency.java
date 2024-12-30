package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("journal_paper_agency")
public class JournalPaperAgency implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long paperId;
    private String affiliationsCode;
    private String affiliationsName;
    private Integer sort;
    private String provinceAndCity;
    private String zipCode;
}
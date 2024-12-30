package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("journal_paper_detail")
public class JournalPaperDetail implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long paperId;
    private String htmlStorePath;
    private String title;
    private String subTitle;
    private String summary;
    private String keywords;
    private String fund;
    private Integer pageCount;
    private String articlePageNumber;
    private String pdfPageNumber;
    private String orderField;
    private String authorNames;
    private String affiliationsName;
    private String doi;
    private String clcs;
    private String pdfUrl;
    private String pdfStorePath;
    private String pdfGrabStatus;
    private Date created;
    private Date updated;
}
package com.journal.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="journal_paper_detail_rule")
@Data
public class JournalPaperDetailRule implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 期刊id
     */
    private Long basicId;

    /**
     * 论文url前缀
     * */
    private String paperUrlPrefix;

    /**
     * 论文页链接
     */
    private String paperUrl;

    /**
     * 是否静态页面
     */
    private Boolean staticPage;

    /**
     * 论文标题规则
     */
    private String titleRule;

    /**
     * 论文摘要规则
     */
    private String abstractRule;

    /**
     * 论文关键词规则
     */
    private String keywordsRule;

    /**
     * 基金规则
     */
    private String fundRule;

    /**
     * 作者规则
     */
    private String authorRule;

    /**
     * 机构规则
     */
    private String deptRule;

    /**
     * doi规则
     */
    private String doiRule;

    /**
     * 分类规则
     */
    private String clcsRule;

    /**
     * pdf规则
     */
    private String pdfRule;

    /**
     * 卷期规则
     */
    private String issueRule;

    private Date created;
    private Date updated;

}
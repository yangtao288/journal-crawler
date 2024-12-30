package com.journal.crawler.enums;

public enum JournalExecuteHistoryPhaseEnum {
    ISSUES(1, "抓取卷期"),
    PAPERS(2, "抓取论文链接"),
    DETAILS(3, "抓取论文详情"),
    PDF_DOWNLOAD(4, "PDF下载"),
    AUTHOR_AGENCY_CLEAN(5, "作者机构清洗");

    private int index;
    private String desc;

    JournalExecuteHistoryPhaseEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
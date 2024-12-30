package com.journal.crawler.task.strategy;

public enum BasicIdKeyEnum {

    DEFAULT("common", -1L),
    HJGCXB("hjgcxb", 1020123L); // 环境工程学报

    private String journalKey;
    private Long basicId;

    BasicIdKeyEnum(String key, Long id) {
        this.journalKey = key;
        this.basicId = id;
    }

    public String getJournalKey() {
        return journalKey;
    }

    public void setJournalKey(String journalKey) {
        this.journalKey = journalKey;
    }

    public Long getBasicId() {
        return basicId;
    }

    public void setBasicId(Long basicId) {
        this.basicId = basicId;
    }

    private static BasicIdKeyEnum findBasicIdKeyEnumByBasicId(Long basicId) {
        for (BasicIdKeyEnum basicIdKeyEnum : BasicIdKeyEnum.values()) {
            if (basicIdKeyEnum.getBasicId().equals(basicId)) {
                return basicIdKeyEnum;
            }
        }
        return BasicIdKeyEnum.DEFAULT;
    }

    public static BasicIdKeyEnum convert(Long basicId) {
        return BasicIdKeyEnum.findBasicIdKeyEnumByBasicId(basicId);
    }
}

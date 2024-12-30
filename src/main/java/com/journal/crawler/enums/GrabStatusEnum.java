package com.journal.crawler.enums;

public enum GrabStatusEnum {
    SUCCESS(1, "SUCCESS"),
    FAILED(2, "FAILED");

    private Integer typeValue;
    private String desc;

    GrabStatusEnum(Integer typeValue, String desc) {
        this.typeValue = typeValue;
        this.desc = desc;
    }

    public Integer getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(Integer typeValue) {
        this.typeValue = typeValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
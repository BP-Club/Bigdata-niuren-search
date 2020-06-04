package com.niuren.dsapi.model.dto;

public class SelectDto {
    /**
     * 字段名称
     */
    private String name;
    /**
     * 聚合
     */
    private String aggs;
    /**
     * true,false
     */
    private String distant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAggs() {
        return aggs;
    }

    public void setAggs(String aggs) {
        this.aggs = aggs;
    }

    public String getDistant() {
        return distant;
    }

    public void setDistant(String distant) {
        this.distant = distant;
    }
}

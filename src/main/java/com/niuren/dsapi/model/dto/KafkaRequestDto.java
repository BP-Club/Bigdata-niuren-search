package com.niuren.dsapi.model.dto;

public class KafkaRequestDto {
    /**
     * 查询语句
     */
    private String sql;

    /**
     * SQL,DSL
     */
    private String status;

    /**
     * 索引
     */
    private String index;

    /**
     * 类型
     */
    private String type;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

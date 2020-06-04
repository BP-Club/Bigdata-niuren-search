package com.niuren.dsapi.model.dto;

public class KafkaDto {
    /**
     * url+端口
     */
    private String url;
    private String topic;
    /**
     * 字段规则
     */
    private Object rule;
    private String tableName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getRule() {
        return rule;
    }

    public void setRule(Object rule) {
        this.rule = rule;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

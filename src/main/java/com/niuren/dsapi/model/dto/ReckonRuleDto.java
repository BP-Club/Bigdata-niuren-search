package com.niuren.dsapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReckonRuleDto {
    private String id;
    private String isDel;
    private String templeCode;
    private String name;
    private String ruleType;
    private String taskStatus;
    private String creator;
    private List<SelectDto> select;

    private List<WhereDto> where;

    private List<GroupDto> groupby;

    private LimitDto limit;

    private KafkaDto kafka;

    public List<SelectDto> getSelect() {
        return select;
    }

    public void setSelect(List<SelectDto> select) {
        this.select = select;
    }

    public List<WhereDto> getWhere() {
        return where;
    }

    public void setWhere(List<WhereDto> where) {
        this.where = where;
    }

    public List<GroupDto> getGroupby() {
        return groupby;
    }

    public void setGroupby(List<GroupDto> groupby) {
        this.groupby = groupby;
    }

    public LimitDto getLimit() {
        return limit;
    }

    public void setLimit(LimitDto limit) {
        this.limit = limit;
    }

    public KafkaDto getKafka() {
        return kafka;
    }

    public void setKafka(KafkaDto kafka) {
        this.kafka = kafka;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getTempleCode() {
        return templeCode;
    }

    public void setTempleCode(String templeCode) {
        this.templeCode = templeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

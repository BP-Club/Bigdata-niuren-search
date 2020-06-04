package com.niuren.dsapi.model.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TempleLogEntity {
    private Long id;

    private String type;

    private Date optTime;

    private String optStatus;

    private String optName;

    private Long templeCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public String getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(String optStatus) {
        this.optStatus = optStatus == null ? null : optStatus.trim();
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName == null ? null : optName.trim();
    }

    public Long getTempleCode() {
        return templeCode;
    }

    public void setTempleCode(Long templeCode) {
        this.templeCode = templeCode;
    }
}
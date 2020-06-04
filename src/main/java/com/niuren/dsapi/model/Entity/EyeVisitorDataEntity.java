package com.niuren.dsapi.model.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "visitorData")
public class EyeVisitorDataEntity {

    /**
     * 访问数量
     */
    @Column(name = "visitor_num")
    private String visitCount;

    /**
     * 客户数
     */
    @Column(name = "mem_num")
    private String customCount;

    /**
     * 一级站点
     */
    @Column(name = "first_site")
    private String firstSite;


    /**
     * 二级站点
     */
    @Column(name = "sec_site")
    private String secSite;

    /**
     * 时间
     */
    @Column(name = "timestamp")
    private Long timestamps;

    /**
     * 品类
     */
    @Column(name = "brand")
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    public String getCustomCount() {
        return customCount;
    }

    public void setCustomCount(String customCount) {
        this.customCount = customCount;
    }

    public String getFirstSite() {
        return firstSite;
    }

    public void setFirstSite(String firstSite) {
        this.firstSite = firstSite;
    }

    public String getSecSite() {
        return secSite;
    }

    public void setSecSite(String secSite) {
        this.secSite = secSite;
    }

    public Long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Long timestamps) {
        this.timestamps = timestamps;
    }
}

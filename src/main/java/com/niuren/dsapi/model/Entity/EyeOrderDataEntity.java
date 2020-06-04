package com.niuren.dsapi.model.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * @author: zsl
 * @description: measurement
 * @create: 2018-05-31 11:37
 **/
@Measurement(name = "orderData")
public class EyeOrderDataEntity {

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
     * 订单量
     */
    @Column(name = "order_num")
    private String orderNum;

    /**
     * 销售额
     */
    @Column(name = "sales_num")
    private String salesNum;
    /**
     * 时间
     */
    @Column(name = "timestamp")
    private Long timestamps;

    /**
     * 一级站点对应客户数
     */
    @Column(name = "order_home_num")
    private String orderHomeNum;

    /**
     * 二级对应的客户数
     */
    @Column(name = "order_sec_num")
    private String orderSecNum;


    public String getOrderHomeNum() {
        return orderHomeNum;
    }

    public void setOrderHomeNum(String orderHomeNum) {
        this.orderHomeNum = orderHomeNum;
    }

    public String getOrderSecNum() {
        return orderSecNum;
    }

    public void setOrderSecNum(String orderSecNum) {
        this.orderSecNum = orderSecNum;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(String salesNum) {
        this.salesNum = salesNum;
    }

    public Long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Long timestamps) {
        this.timestamps = timestamps;
    }
}

package com.niuren.dsapi.model.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * @author: zsl
 * @description: measurement
 * @create: 2018-05-31 11:37
 **/
@Measurement(name = "tt")
public class DataEyeQueryEntity {


    /**
     * 访问数量
     */
    @Column(name = "VisitorSum")
    private String visitCount;

    /**
     * 客户数
     */
    @Column(name = "MemeberSum")
    private String customCount;


    /**
     * 销售额度
     */
    @Column(name = "TotalPrice")
    private String sale;

    /**
     * 订单数量
     */
    @Column(name = "OrderSum")
    private String orderCount;


    /**
     * 站点
     */
    @Column(name = "TAG_CODE")
    private String siteCode;

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

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}

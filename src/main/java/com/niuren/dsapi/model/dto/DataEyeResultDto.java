package com.niuren.dsapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.List;

/**
 * 直播间数据查询返回
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DataEyeResultDto implements Serializable {
    /**
     * 访问数量
     */
    private String visitCount;

    /**
     * 客户数
     */
    private String customCount;
    /**
     * 转化率
     */
    private String convert;

    /**
     * 销售额度
     */
    private String sale;

    /**
     * 订单数量
     */
    private String orderCount;

    /**
     * 客单价
     */
    private String customMoney;

    /**
     * 站点
     */
    private String siteCode;

    /**
     * 销售额排行
     */
    private List<DateEyeSiteDto> saleList;

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

    public String getConvert() {
        return convert;
    }

    public void setConvert(String convert) {
        this.convert = convert;
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

    public String getCustomMoney() {
        return customMoney;
    }

    public void setCustomMoney(String customMoney) {
        this.customMoney = customMoney;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public List<DateEyeSiteDto> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<DateEyeSiteDto> saleList) {
        this.saleList = saleList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DataEyeResultDto{");
        sb.append("visitCount='").append(visitCount).append('\'');
        sb.append(", customCount='").append(customCount).append('\'');
        sb.append(", convert='").append(convert).append('\'');
        sb.append(", sale='").append(sale).append('\'');
        sb.append(", orderCount='").append(orderCount).append('\'');
        sb.append(", customMoney='").append(customMoney).append('\'');
        sb.append(", siteCode='").append(siteCode).append('\'');
        sb.append(", saleList=").append(saleList);
        sb.append('}');
        return sb.toString();
    }
}

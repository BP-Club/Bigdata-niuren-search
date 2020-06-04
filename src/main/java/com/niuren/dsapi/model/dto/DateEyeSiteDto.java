package com.niuren.dsapi.model.dto;

import com.niuren.dsapi.util.StringUtil;

public class DateEyeSiteDto {
    //站点代号
    private String siteCode;
    //销售额度
    private String sale;

    /**
     * 访问数量
     */
    private String visitCount;

    /**
     * 客户数
     */
    private String customCount;

    /**
     * 订单数量
     */
    private String orderCount;

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        if (StringUtil.isEmpty(visitCount)) {
            this.visitCount = "0.00";
        } else {
            this.visitCount = visitCount;
        }
    }

    public String getCustomCount() {
        return customCount;
    }

    public void setCustomCount(String customCount) {
        if (StringUtil.isEmpty(customCount)) {
            this.customCount = "0.00";
        } else {
            this.customCount = customCount;
        }
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        if (StringUtil.isEmpty(orderCount)) {
            this.orderCount = "0.00";
        } else {
            this.orderCount = orderCount;
        }
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        if (StringUtil.isEmpty(sale)) {
            this.sale = "0.00";
        } else {
            this.sale = sale;
        }
    }
}

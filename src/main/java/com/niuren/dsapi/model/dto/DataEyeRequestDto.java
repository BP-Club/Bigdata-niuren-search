package com.niuren.dsapi.model.dto;

public class DataEyeRequestDto {
    /**
     * 查询日期 yyyy-mm-dd
     */
    private String date;

    /**
     * 一级站点
     */
    private String oneSite;
    /**
     * 二级站点
     */
    private String twoSite;

    /**
     * 品类
     */
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOneSite() {
        return oneSite;
    }

    public void setOneSite(String oneSite) {
        this.oneSite = oneSite;
    }

    public String getTwoSite() {
        return twoSite;
    }

    public void setTwoSite(String twoSite) {
        this.twoSite = twoSite;
    }
}

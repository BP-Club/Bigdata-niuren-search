package com.niuren.dsapi.model.dto;

public class NumtrendRgstResultDto {

    /**
     * 时间
     */
    private String time;

    //用户增长量
    private String user_growth;

    //环比增长率
    private String annulus_growth;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_growth() {
        return user_growth;
    }

    public void setUser_growth(String user_growth) {
        this.user_growth = user_growth;
    }

    public String getAnnulus_growth() {
        return annulus_growth;
    }

    public void setAnnulus_growth(String annulus_growth) {
        this.annulus_growth = annulus_growth;
    }
}

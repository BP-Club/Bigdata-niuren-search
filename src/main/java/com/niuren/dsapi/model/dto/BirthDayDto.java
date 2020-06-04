package com.niuren.dsapi.model.dto;

/**
 * 注册日期
 */
public class BirthDayDto {
    //用户生日区间起始值
    private  String  floorValue;
    //用户生日区间结束值
    private  String  ceilValue;

    public String getFloorValue() {
        return floorValue;
    }

    public void setFloorValue(String floorValue) {
        this.floorValue = floorValue;
    }

    public String getCeilValue() {
        return ceilValue;
    }

    public void setCeilValue(String ceilValue) {
        this.ceilValue = ceilValue;
    }
}

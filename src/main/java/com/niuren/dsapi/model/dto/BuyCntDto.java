package com.niuren.dsapi.model.dto;

/**
 * 用户交易笔数
 */
public class BuyCntDto {
    /**
     *用户交易笔数区间起始值
     */
    private  Integer floorValue;
    /**
     *用户交易笔数区间结束值
     */
    private  Integer ceilValue;

    public Integer getFloorValue() {
        return floorValue;
    }

    public void setFloorValue(Integer floorValue) {
        this.floorValue = floorValue;
    }

    public Integer getCeilValue() {
        return ceilValue;
    }

    public void setCeilValue(Integer ceilValue) {
        this.ceilValue = ceilValue;
    }
}

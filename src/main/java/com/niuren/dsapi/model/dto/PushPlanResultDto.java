package com.niuren.dsapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * push点击
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PushPlanResultDto implements Serializable {
    private String orderCnt;//订单数
    private String clickCnt;//点击次数
    private String branchName;//分支名称
    private String pv;//pv
    private String orderAmt;//订单金额
    private String lastUpdateTime;//更新时间

    public String getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(String orderCnt) {
        this.orderCnt = orderCnt;
    }

    public String getClickCnt() {
        return clickCnt;
    }

    public void setClickCnt(String clickCnt) {
        this.clickCnt = clickCnt;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}

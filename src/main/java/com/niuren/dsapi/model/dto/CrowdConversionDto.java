package com.niuren.dsapi.model.dto;

public class CrowdConversionDto {

    //default
    private String branch_name;
    //用户数
    private String memberCnt;
    //交易笔数
    private String buyCnt;
    //交易金额
    private String buyMoney;

    private String lastUpdateTime;

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getMemberCnt() {
        return memberCnt;
    }

    public void setMemberCnt(String memberCnt) {
        this.memberCnt = memberCnt;
    }

    public String getBuyCnt() {
        return buyCnt;
    }

    public void setBuyCnt(String buyCnt) {
        this.buyCnt = buyCnt;
    }

    public String getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(String buyMoney) {
        this.buyMoney = buyMoney;
    }
}

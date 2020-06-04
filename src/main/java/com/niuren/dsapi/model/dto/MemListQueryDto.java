package com.niuren.dsapi.model.dto;

import java.util.List;

public class MemListQueryDto {
    // 是否订阅：1-是 0-否
    private String subscribeflag;

    //客户来源id  7-普通用户 11-facebook用户 12-google用户 13VK用户
    private List<Integer> accountType;

    /**
     * 会员等级 1:注册 2:V0 3:V1 4:V2 5:V3
     */
    private List<Integer> level;

    /**
     * 会员等级 1:注册 2:V0 3:V1 4:V2 5:V3
     */
    private List<String> emailSuffix;

    /**
     * 注册站点 对应商城site_from
     */
    private List<String> siteUid;

    /**
     * 交易笔数
     */
    private BuyCntDto buyCnt;

    /**
     * 注册日期
     */
    private RegisterTimeDto registerTime;
    //用户最近一笔订单输入的手机号
    private List<String> shppPhoneNo;
    //用户邮箱
    private List<String> email;
    //是否为邮件黑名单（单选）：EDM邮件退订组   1:是  0：否
    private Integer emailBlacklistFlag;
    //昵称
    private List<String> nickName;
    //用户市场  1~11
    private List<Integer> marketId;
    //生日日期
    private BirthDayDto birthday;

    //邮箱是否验证  1:是  0:否
    private Integer emailVerifyFlag;

    //邮件是否订阅  1-已订阅 0-未订阅 2-退订
    private Integer subscribeFlag;

    //是否为黑名单用户  1:是  0:否
    private Integer blackFlag;

    //用户最近一笔订单国家进行筛选 id为商城国家Id
    private List<Integer> shppCountryId;

    public String getSubscribeflag() {
        return subscribeflag;
    }

    public void setSubscribeflag(String subscribeflag) {
        this.subscribeflag = subscribeflag;
    }

    public List<Integer> getAccountType() {
        return accountType;
    }

    public void setAccountType(List<Integer> accountType) {
        this.accountType = accountType;
    }

    public List<Integer> getLevel() {
        return level;
    }

    public void setLevel(List<Integer> level) {
        this.level = level;
    }

    public List<String> getEmailSuffix() {
        return emailSuffix;
    }

    public void setEmailSuffix(List<String> emailSuffix) {
        this.emailSuffix = emailSuffix;
    }

    public List<String> getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(List<String> siteUid) {
        this.siteUid = siteUid;
    }

    public BuyCntDto getBuyCnt() {
        return buyCnt;
    }

    public void setBuyCnt(BuyCntDto buyCnt) {
        this.buyCnt = buyCnt;
    }

    public RegisterTimeDto getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(RegisterTimeDto registerTime) {
        this.registerTime = registerTime;
    }

    public List<String> getShppPhoneNo() {
        return shppPhoneNo;
    }

    public void setShppPhoneNo(List<String> shppPhoneNo) {
        this.shppPhoneNo = shppPhoneNo;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public Integer getEmailBlacklistFlag() {
        return emailBlacklistFlag;
    }

    public void setEmailBlacklistFlag(Integer emailBlacklistFlag) {
        this.emailBlacklistFlag = emailBlacklistFlag;
    }

    public List<String> getNickName() {
        return nickName;
    }

    public void setNickName(List<String> nickName) {
        this.nickName = nickName;
    }

    public List<Integer> getMarketId() {
        return marketId;
    }

    public void setMarketId(List<Integer> marketId) {
        this.marketId = marketId;
    }

    public BirthDayDto getBirthday() {
        return birthday;
    }

    public void setBirthday(BirthDayDto birthday) {
        this.birthday = birthday;
    }

    public Integer getEmailVerifyFlag() {
        return emailVerifyFlag;
    }

    public void setEmailVerifyFlag(Integer emailVerifyFlag) {
        this.emailVerifyFlag = emailVerifyFlag;
    }

    public Integer getSubscribeFlag() {
        return subscribeFlag;
    }

    public void setSubscribeFlag(Integer subscribeFlag) {
        this.subscribeFlag = subscribeFlag;
    }

    public Integer getBlackFlag() {
        return blackFlag;
    }

    public void setBlackFlag(Integer blackFlag) {
        this.blackFlag = blackFlag;
    }

    public List<Integer> getShppCountryId() {
        return shppCountryId;
    }

    public void setShppCountryId(List<Integer> shppCountryId) {
        this.shppCountryId = shppCountryId;
    }


}

package com.niuren.dsapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 人群报表导出
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CrowdExportResultDto {

    //日期 YYYY-MM-DD
    private String time;

    //终端类型
    private String terminal;

    //二级站点
    private String site_uid;

    //人群总数
    private String total_num;

    //昨日访客
    private String yesterday_visitors_num;

    //昨日成交
    private String yesterday_transactions_num;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getSite_uid() {
        return site_uid;
    }

    public void setSite_uid(String site_uid) {
        this.site_uid = site_uid;
    }

    public String getTotal_num() {
        return total_num;
    }

    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }

    public String getYesterday_visitors_num() {
        return yesterday_visitors_num;
    }

    public void setYesterday_visitors_num(String yesterday_visitors_num) {
        this.yesterday_visitors_num = yesterday_visitors_num;
    }

    public String getYesterday_transactions_num() {
        return yesterday_transactions_num;
    }

    public void setYesterday_transactions_num(String yesterday_transactions_num) {
        this.yesterday_transactions_num = yesterday_transactions_num;
    }
}

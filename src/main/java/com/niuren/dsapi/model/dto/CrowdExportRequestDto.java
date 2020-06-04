package com.niuren.dsapi.model.dto;

import java.util.List;

/**
 * 会员分群数据导出
 */
public class CrowdExportRequestDto {
    /**
     * 品牌
     */
    private String brand;
    /**
     * 人群id
     */
    private String crowd_id;
    /**
     * 市场id
     */
    private String market_id;
    /**
     * 站点
     */
    private List<String> site_uids;
    /**
     * 终端
     */
    private String terminal_type;

    /**
     * 开始时间 YYYY-MM-DD
     */
    private String start_time;
    /**
     * 结束时间 YYYY-MM-DD
     */
    private String end_time;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCrowd_id() {
        return crowd_id;
    }

    public void setCrowd_id(String crowd_id) {
        this.crowd_id = crowd_id;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public List<String> getSite_uids() {
        return site_uids;
    }

    public void setSite_uids(List<String> site_uids) {
        this.site_uids = site_uids;
    }

    public String getTerminal_type() {
        return terminal_type;
    }

    public void setTerminal_type(String terminal_type) {
        this.terminal_type = terminal_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}

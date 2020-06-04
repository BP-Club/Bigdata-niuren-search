package com.niuren.dsapi.model.dto;

import java.util.List;

public class FrstorderDto {
    //品牌
    private String brand;
    //市场id
    private String market_id;
    //站点id
    private List<String> site_uids;
    //终端“PC”,”M”]
    private String terminal_type;
    //日期
    private String date;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

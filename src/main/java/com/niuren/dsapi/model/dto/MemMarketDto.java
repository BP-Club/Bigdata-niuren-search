package com.niuren.dsapi.model.dto;

import java.io.Serializable;
import java.util.List;

public class MemMarketDto implements Serializable {
    //市场id
    private String market_id;

    //品牌
    private String brand;

    //终端   单选   当传APP 对应查询 IOS,ANDROID
    private String terminal_type;

    //站点id -1 为全部
    private List<String> site_uids;

    //人群id
    private List<PeopleGroupDto> follow_list;


    public List<PeopleGroupDto> getFollow_list() {
        return follow_list;
    }

    public void setFollow_list(List<PeopleGroupDto> follow_list) {
        this.follow_list = follow_list;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTerminal_type() {
        return terminal_type;
    }

    public void setTerminal_type(String terminal_type) {
        this.terminal_type = terminal_type;
    }

    public List<String> getSite_uids() {
        return site_uids;
    }

    public void setSite_uids(List<String> site_uids) {
        this.site_uids = site_uids;
    }
}

package com.niuren.dsapi.model.dto;

import java.util.List;

/**
 * 用户量的趋势查询
 */
public class NumtrendRgstDto {
    /**
     * 品牌-指的是哪个门户网站
     */
    private String brand;

    /**
     * 按月：0，按年：1
     */
    private String date_type;

    /**
     * 当date_type为1 YYYY 当date_type为0 2018-01
     */
    private String stat_date;

    /**
     * 市场id（美国，欧盟等划分） 如果为-1查询所有
     */
    private String market_id;

    /**
     * （国家语言划分，西班牙，英语）站点id  如果为-1查询所有
     */
    private List<String> site_uids;


    /**
     * 终端：PC,APP,如果传APP 筛选“IOS”,”ANDIORD” 如果为-1为所有,值为单个值
     */
    private String terminal_type;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDate_type() {
        return date_type;
    }

    public void setDate_type(String date_type) {
        this.date_type = date_type;
    }

    public String getStat_date() {
        return stat_date;
    }

    public void setStat_date(String stat_date) {
        this.stat_date = stat_date;
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
}

package com.niuren.dsapi.model.dto;

import java.util.List;

public class CrowdSiteUidReqDto {
    private  String  brand;

    private List<String> site_uid;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getSite_uid() {
        return site_uid;
    }

    public void setSite_uid(List<String> site_uid) {
        this.site_uid = site_uid;
    }
}

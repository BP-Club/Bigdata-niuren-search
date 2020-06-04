package com.niuren.dsapi.model.dto;

import java.util.List;

/**
 *
 */
public class CrowdSiteidResultDto {

    private List<Double> crowd_list;

    private List<CrowdSiteidDto> data;

    public List<Double> getCrowd_list() {
        return crowd_list;
    }

    public void setCrowd_list(List<Double> crowd_list) {
        this.crowd_list = crowd_list;
    }

    public List<CrowdSiteidDto> getData() {
        return data;
    }

    public void setData(List<CrowdSiteidDto> data) {
        this.data = data;
    }
}

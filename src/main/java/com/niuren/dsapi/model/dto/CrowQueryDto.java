package com.niuren.dsapi.model.dto;

import java.util.List;

public class CrowQueryDto {

    private String crowd_id;

    private CrowdExportResultDto data;

    public String getCrowd_id() {
        return crowd_id;
    }

    public void setCrowd_id(String crowd_id) {
        this.crowd_id = crowd_id;
    }

    public CrowdExportResultDto getData() {
        return data;
    }

    public void setData(CrowdExportResultDto data) {
        this.data = data;
    }
}

package com.niuren.dsapi.model.dto;

import java.util.List;

public class CrowdConversionResultDto {
    private String sendDate;

    private List<CrowdConversionDto> data;

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public List<CrowdConversionDto> getData() {
        return data;
    }

    public void setData(List<CrowdConversionDto> data) {
        this.data = data;
    }
}

package com.niuren.dsapi.model.dto;

import com.niuren.dsapi.model.dto.CrowdExportResultDto;

import java.util.List;

public class CrowdExportResDto {
    private String time;

    private List<CrowdExportResultDto> detail;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<CrowdExportResultDto> getDetail() {
        return detail;
    }

    public void setDetail(List<CrowdExportResultDto> detail) {
        this.detail = detail;
    }
}

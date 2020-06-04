package com.niuren.dsapi.model.dto;

import java.util.List;

public class NumtrendResultDto {

    private String time;

    private List<NumtrendDto> data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<NumtrendDto> getData() {
        return data;
    }

    public void setData(List<NumtrendDto> data) {
        this.data = data;
    }
}

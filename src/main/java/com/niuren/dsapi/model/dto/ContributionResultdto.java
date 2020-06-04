package com.niuren.dsapi.model.dto;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 贡献查询json返回
 */
public class ContributionResultdto {

    private String time;

    private List<ContributionEnDto> data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<ContributionEnDto> getData() {
        return data;
    }

    public void setData(List<ContributionEnDto> data) {
        this.data = data;
    }
}

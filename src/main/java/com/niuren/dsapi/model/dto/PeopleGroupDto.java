package com.niuren.dsapi.model.dto;

import java.io.Serializable;

/**
 * 人群
 */
public class PeopleGroupDto implements Serializable {

    //人群ID
    private String crowd_id;

    //查询日期
    private String dt;

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getCrowd_id() {
        return crowd_id;
    }

    public void setCrowd_id(String crowd_id) {
        this.crowd_id = crowd_id;
    }
}

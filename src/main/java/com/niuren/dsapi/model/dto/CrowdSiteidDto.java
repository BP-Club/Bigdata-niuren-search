package com.niuren.dsapi.model.dto;

/**
 * 会员分群按照站点查询
 */
public class CrowdSiteidDto {
    private String crowd_id;

    private String total_num;

    public String getCrowd_id() {
        return crowd_id;
    }

    public void setCrowd_id(String crowd_id) {
        this.crowd_id = crowd_id;
    }

    public String getTotal_num() {
        return total_num;
    }

    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }
}

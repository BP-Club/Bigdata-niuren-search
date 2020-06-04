package com.niuren.dsapi.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 触发push数据处理
 */
public class PushPlanRequestDto implements Serializable {
    private String planId;//计划id
    private String flag;//01汇总，02明细

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}

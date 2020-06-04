package com.niuren.dsapi.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 触发push数据处理
 */
public class PushTaskRequestDto implements Serializable {
    private String plan_id;//策略id
    private String task_id; //任务id
    private String plan_type;//类型：push
    private List<Object> crowd_id; //人群id
    private List<Object> upload_push_crowd_id;//上传人群
    private String crowd_type;//人群类型  0:指定用户  1： 人群细分
    private List<String> site_uid; //站点id
    private String abt_flag;  // 0：关闭ABT  1：开启ABT
    private List<Object> abt_content;  //ABT流量比例
    private List<Object> language_content;//语言

    public List<Object> getCrowd_id() {
        return crowd_id;
    }

    public void setCrowd_id(List<Object> crowd_id) {
        this.crowd_id = crowd_id;
    }

    public List<Object> getUpload_push_crowd_id() {
        return upload_push_crowd_id;
    }

    public void setUpload_push_crowd_id(List<Object> upload_push_crowd_id) {
        this.upload_push_crowd_id = upload_push_crowd_id;
    }

    public List<Object> getLanguage_content() {
        return language_content;
    }

    public void setLanguage_content(List<Object> language_content) {
        this.language_content = language_content;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getPlan_type() {
        return plan_type;
    }

    public void setPlan_type(String plan_type) {
        this.plan_type = plan_type;
    }


    public String getCrowd_type() {
        return crowd_type;
    }

    public void setCrowd_type(String crowd_type) {
        this.crowd_type = crowd_type;
    }

    public List<String> getSite_uid() {
        return site_uid;
    }

    public void setSite_uid(List<String> site_uid) {
        this.site_uid = site_uid;
    }

    public String getAbt_flag() {
        return abt_flag;
    }

    public void setAbt_flag(String abt_flag) {
        this.abt_flag = abt_flag;
    }

    public List<Object> getAbt_content() {
        return abt_content;
    }

    public void setAbt_content(List<Object> abt_content) {
        this.abt_content = abt_content;
    }
}

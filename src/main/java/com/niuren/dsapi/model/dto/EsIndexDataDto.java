package com.niuren.dsapi.model.dto;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class EsIndexDataDto implements Serializable {
    private String index;

    private String type;

    private String id;

    private JSONObject jsonObj;


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getJsonObj() {
        return jsonObj;
    }

    public void setJsonObj(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EsIndexDataDto{");
        sb.append("index='").append(index).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", jsonObj=").append(jsonObj);
        sb.append('}');
        return sb.toString();
    }
}

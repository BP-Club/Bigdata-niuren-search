package com.niuren.dsapi.model.dto;

import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * Sql 模板请求
 */
@ToString
public class SqlTempleRequestDto implements Serializable {
    /**
     * 请求模板Code
     */
    private String templeCode;

    /**
     * ES ,HIVE,HBASE
     */
    private String type;


    /**
     * 请求参数
     */
    private Map<String, Object> paramsMap;

    public String getTempleCode() {
        return templeCode;
    }

    public void setTempleCode(String templeCode) {
        this.templeCode = templeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }
}

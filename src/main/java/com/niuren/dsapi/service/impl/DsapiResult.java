package com.niuren.dsapi.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.niuren.dsapi.config.Constant;

import java.util.HashMap;

/**
 * 统一返回规范
 */
public class DsapiResult extends HashMap<String, Object> {
    //未登陆
    public static final DsapiResult FALSE_NOT_LOGIN = new DsapiResult(false, Constant.NOT_LOGIN, "未登录", null);
    //成功登录
    public static final DsapiResult TRUE_REST_API_RESULT = new DsapiResult();


    /**
     * @param success   true,//状态true，false
     * @param errorCode 500,//errorCode
     * @param errorMsg  '',//errorMsg
     * @param data      {}//返回数据
     */
    public DsapiResult(Boolean success, Integer errorCode, String errorMsg, Object data) {
        super(4);
        this.put("success", success);
        this.put("errorCode", errorCode);
        this.put("errorMsg", errorMsg);
        this.put("data", data);
    }


    /**
     * 错误时返回
     *
     * @param errorCode
     * @param errorMsg
     */
    public DsapiResult(Integer errorCode, String errorMsg) {
        super(4);
        this.put("success", false);
        this.put("errorCode", errorCode);
        this.put("errorMsg", errorMsg);
        this.put("data", null);
    }

    /**
     * 操作成功，有返回数据
     *
     * @param data
     */
    public DsapiResult(Object data) {
        super(4);
        this.put("success", true);
        this.put("errorCode", 0);
        this.put("errorMsg", "");
        this.put("data", data);
    }

    /**
     * 操作成功，无返回数据
     */
    private DsapiResult() {
        super(4);
        this.put("success", true);
        this.put("errorCode", 0);
        this.put("errorMsg", "");
        this.put("data", null);
    }


    public boolean isSuccess() {
        return Boolean.parseBoolean(get("success").toString());
    }


    public int getErrorCode() {
        return Integer.parseInt(get("errorCode").toString());
    }


    public String getErrorMsg() {
        return get("errorMsg").toString();
    }


    public Object getData() {
        return get("data");
    }
}

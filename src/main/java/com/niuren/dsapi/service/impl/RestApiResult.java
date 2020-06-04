package com.niuren.dsapi.service.impl;

import com.niuren.dsapi.config.Constant;

import java.util.HashMap;

/**
 * 基于之前的数据服务交互规范
 *
 * @author zsl
 */
public class RestApiResult extends HashMap<String, Object> {

    /**
     * 返回参数
     *
     * @param code     错误码
     * @param errorMsg 错误消息
     * @param data     数据
     */
    public RestApiResult(int code, String errorMsg, Object data, Object info) {
        this.put("code", code);
        this.put("msg", errorMsg);
        this.put("data", data);
        this.put("info", info);
    }


    /**
     * success
     *
     * @param data
     */
    public RestApiResult(Object data) {
        this.put("code", "0");
        this.put("msg", "ok");
        this.put("info", data);
    }


    /**
     * 异常错误
     */
    public RestApiResult(Exception e) {
        this.put("code", Constant.ERROR_NODATA);
        this.put("msg", e.getMessage());
        Object data = null;
        if (data == null) {
            this.put("data", new Object());
        } else {
            this.put("info", data);
        }
    }

    /**
     * 返回参数-带总数
     * @param total 错误消息
     * @param data     数据
     */
    public RestApiResult(Object data, Integer total) {
        this.put("code", "0");
        this.put("msg", "ok");
        this.put("info", data);
        this.put("total", total);

    }


}

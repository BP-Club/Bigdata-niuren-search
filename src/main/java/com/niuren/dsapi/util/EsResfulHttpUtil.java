package com.niuren.dsapi.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dailinwei
 * @version 1.0
 * @description get post工具类
 * @Create 2017-06-16
 */
public class EsResfulHttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsResfulHttpUtil.class);
    private static final OkHttpClient CLIENT;

    static {
        CLIENT = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                .writeTimeout(30, TimeUnit.SECONDS) //写超时
                .readTimeout(30, TimeUnit.SECONDS) //读超时
                .build();
    }

    public enum OkHttpMethod {
        POST,
        PUT,
        DELETE
    }

    /**
     * get请求,支持http和https
     *
     * @param url     地址,比如: http://wwww.baidu.com
     * @param params  参数,可以为null
     * @param headers 请求头,可以为null
     * @return
     */
    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        //Builder对象
        Request.Builder builder = new Request.Builder();

        //处理参数
        if (null != params && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("?");
            params.forEach((k, v) -> {
                stringBuilder.append(k).append("=").append(v).append("&");
            });
            String param = stringBuilder.toString();
            url += param.substring(0, param.lastIndexOf("&"));
        }

        //处理请求头
        if (null != headers && headers.size() > 0) {
            headers.forEach((k, v) -> builder.header(k, v));
        }

        Request request = builder.url(url).build();

        //创建响应对象
        try {
            Response response = CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                LOGGER.error("发送get请求失败,状态码:{}", response.code());
                return "";
            }
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("发送get请求失败,原因:{}", e.getCause());
            return "";
        }
    }

    /**
     * post,put,delete请求,支持http和https
     *
     * @param url          地址,比如: http://wwww.baidu.com
     * @param jsonParams   请求参数 json格式字符串
     * @param headers      请求头,可以为null
     * @param okHttpMethod 请求方式
     * @return
     */
    public static String postOrPutOrDelete(String url, String jsonParams, Map<String, String> headers, OkHttpMethod okHttpMethod) {
        //Builder对象
        Request.Builder builder = new Request.Builder();
        LOGGER.info("postOrPutOrDelete-begin url="+url);
        //处理请求头
        if (null != headers && headers.size() > 0) {
            headers.forEach((k, v) -> builder.header(k, v));
        }
        //处理参数
        if (!StringUtils.isEmpty(jsonParams)) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
            switch (okHttpMethod) {
                case POST:
                    builder.post(body);
                    break;
                case PUT:
                    builder.put(body);
                    break;
                case DELETE:
                    builder.delete(body);
                    break;
                default:
                    builder.post(body);
                    break;

            }
        } else {
            switch (okHttpMethod) {
                case DELETE:
                default:
                    builder.delete();
                    break;
            }
        }
        Request request = builder.url(url).build();
        //创建响应对象
        try {
            Response response = CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                LOGGER.error("发送请求失败,状态码:{}", response.code());
                return "";
            }
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("发送请求失败,原因:{}", e.getCause());
            return "";
        }finally {
            LOGGER.info("postOrPutOrDelete-end");
        }
    }

}
package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceEsConfig;
import com.niuren.dsapi.config.SysDataSource;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.model.dto.EsRequestDto;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.EsDataQueryApi;
import com.niuren.dsapi.util.EsNodeClientUtil;
import com.niuren.dsapi.util.EsResfulHttpUtil;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EsDataQueryImp implements EsDataQueryApi {

    private static final Logger log = LoggerFactory.getLogger(EsDataQueryImp.class);
    @Autowired
    DataSourceEsConfig esConfig;


    @Autowired
    private SysDataSource sysDataSource;

    /**
     * 1.es数据查询
     *
     * @param requestDto
     * @return
     */
    public DsapiResult queryEsData(EsRequestDto requestDto) {
        log.info("EsDataQueryImp-queryEsData  begin");
        //1:解析DSL语句
        String resultJson = "";
        try {
            String dsl = "";
            //默认执行SQL
            if (StringUtil.isEmpty(requestDto.getStatus()) || "SQL".equals(requestDto.getStatus())) {
                dsl = explain(requestDto.getSql());
            } else {
                dsl = requestDto.getSql();
            }
            log.info("request-explain dsl=" + dsl);

            //2：执行数据请求
            String rsURL = "";
            rsURL = esConfig.getPostUrl() + "/" + requestDto.getIndex();
            //类型
            if (StringUtil.isNotEmpty(requestDto.getType())) {
                rsURL += "/" + requestDto.getType();
            }
            rsURL += "/_search";
            log.info("request-url=" + rsURL);
            resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, dsl, null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

        return new DsapiResult(JSON.parseObject(resultJson));
    }

    /**
     * 2.es数据查询-aws仅支持单表查询
     *
     * @param index
     * @return
     */
    public DsapiResult queryEsDataBySql(String index, String sql, String sourceType) {
        log.info("EsDataQueryImp-queryEsData  begin");
        //1:解析DSL语句
        String resultJson = "";
        try {
            String dsl = explain(sql);
            log.info("request-explain dsl=" + dsl);
            String url = sysDataSource.getEsSource(sourceType);

            if (StringUtil.isEmpty(url)) {
                log.warn("**************explain  can't find  url**************");
                throw new ServiceException(" query fail because can't find es url");
            }
            //2：执行数据请求
            String rsURL = url + "/"+index + "/_search";
            //String rsURL = url + "/"+"indextest002"+"/_search";
            log.info("request-url=" + rsURL);
            log.info("request-json=" + dsl);
            resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, dsl, null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

        return new DsapiResult(JSON.parseObject(resultJson));
    }

    /**
     * 3.sql数据解析DSL
     *
     * @param sql
     * @return
     */
    public String explain(String sql) {
        log.info("****************EsDataQueryImp-explain  begin****************");
        String explanSql = "";

        if (StringUtil.isEmpty(sql)) {
            log.warn("**************explain  can't find  sql**************");
            throw new ServiceException("explain dsl fail because can't find  sql");
        }

        try {
            explanSql = EsNodeClientUtil.getClient().explain(sql.trim()).explain().explain();
        } catch (Exception e) {
            throw new ServiceException("explain dsl fail :" + e.getMessage());
        }

        return explanSql;
    }

}

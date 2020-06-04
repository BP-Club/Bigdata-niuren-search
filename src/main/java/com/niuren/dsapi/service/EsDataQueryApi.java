package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.EsRequestDto;
import com.niuren.dsapi.service.impl.DsapiResult;

public interface EsDataQueryApi {


    /**
     * 1.es数据查询
     *
     * @param requestDto
     * @return
     */
    public DsapiResult queryEsData(EsRequestDto requestDto);

    /**
     * 单表查询基于AWS
     *
     * @param index      索引
     * @param sql        查询sql
     * @param sourceType 数据源
     * @return
     */
    public DsapiResult queryEsDataBySql(String index, String sql, String sourceType);

    /**
     * 2.sql数据解析DSL
     *
     * @param sql
     * @return
     */
    public String explain(String sql);


}

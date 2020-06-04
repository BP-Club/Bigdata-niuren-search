package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.EsRequestDto;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.EsDataQueryApi;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.service.prometheus.PrometheusService;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * es 数据基础服务
 */

@RestController
@RequestMapping("/api/data")
public class EsDataController {
    private static final Logger log = LoggerFactory.getLogger(EsDataController.class);
    @Autowired
    EsDataQueryApi esDataQueryApi;


    @Autowired
    SqlTempleOptApi sqlTempleOptApi;
    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    PrometheusService prometheusService;

    /**
     * 1:基于http协议:es 查询
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryEsData")
    public DsapiResult queryEsData(@RequestBody EsRequestDto requestDto) {
        log.info("EsDataController-queryEsData sql=" + JSON.toJSONString(requestDto));
        if (requestDto == null || StringUtils.isEmpty(requestDto.getSql()) || StringUtils.isEmpty(requestDto.getIndex())) {
            log.warn("query sql is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        return esDataQueryApi.queryEsData(requestDto);
    }

    /**
     * 执行ES数据查询
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryEsSql")
    public DsapiResult queryEsSql(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("EsDataController-queryEsSql sql=" + JSON.toJSONString(requestDto));
        //解析sql
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "templeCode or index is empty please check !");
        }
        try {
            //查询模板
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            //执行SQL解析
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            //根据sql获取索引
            String index = StringUtil.getIndex(sql);
            //执行查询-单表查询
            DsapiResult result = esDataQueryApi.queryEsDataBySql(index, sql, sqlEntity.getSourceCode());

            // 加入监控
            prometheusService.incrementQueryEsSql(sqlEntity.getTempleCode().toString(), sqlEntity.getCreator());
            return result;
        } catch (Exception e) {
            log.error("HiveDataController-queryBySql error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

    /**
     * 匹配SQL解析
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryEsAnalyze")
    public DsapiResult querySqlAnalyze(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("EsDataController-queryEsAnalyze requestDto=" + JSON.toJSONString(requestDto));
        try {
            if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
                return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
            }
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            //执行Sql 解析
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            //执行DSL解析
            String esDSL = esDataQueryApi.explain(sql);
            return new DsapiResult(JSON.parseObject(esDSL));
        } catch (Exception e) {
            log.error("EsDataController-querySqlAnalyze error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

}

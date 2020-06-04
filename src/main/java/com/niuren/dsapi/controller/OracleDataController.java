package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.exception.TemplateCheckException;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.OracleQueryService;
import com.niuren.dsapi.service.PrestoService;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.service.prometheus.PrometheusService;
import com.niuren.dsapi.util.StringUtil;
import com.niuren.dsapi.util.TemplateCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * es 数据基础服务
 */

@RestController
@RequestMapping("/api/data")
public class OracleDataController {


    private static final Logger log = LoggerFactory.getLogger(OracleDataController.class);
    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    @Resource
    private OracleQueryService oracleQueryService;

    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    PrometheusService prometheusService;


    /**
     * HIVE 查询SQL
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryOracleSql")
    public DsapiResult queryOracleSql(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("OracleDataController-queryOracleSql requestDto=" + JSON.toJSONString(requestDto));
        //解析sql
        List<Map<String, Object>> result = null;
        //校验
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
        }
        try {
            // 检测传入的参数是否合法
            TemplateCheckUtil.checkTempleRequestDto(requestDto);

            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            result = oracleQueryService.query(sql);

            // 加入监控
            prometheusService.incrementQueryOracleSql(sqlEntity.getTempleCode().toString(), sqlEntity.getCreator());

        } catch (Exception e) {
            log.error("OracleDataController-queryOracleSql error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        return new DsapiResult(result);
    }

}

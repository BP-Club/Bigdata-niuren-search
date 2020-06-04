package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.exception.TemplateCheckException;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
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

/**
 * es 数据基础服务
 */

@RestController
@RequestMapping("/api/data")
public class HiveDataController {

    private static final Logger log = LoggerFactory.getLogger(HiveDataController.class);
    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    @Resource
    private PrestoService prestoService;

    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    private PrometheusService prometheusService;


    /**
     * HIVE 查询SQL
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryHiveSqltest")
    public DsapiResult queryHiveSqltest(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("HiveDataController-queryHiveSqltest requestDto=" + JSON.toJSONString(requestDto));
        long beginTime = System.currentTimeMillis();
        log.info("HiveDataController-queryHiveSqltest tempCode= " + requestDto.getTempleCode() + ",beginTime=" + beginTime);
        //解析sql
        Object result = new Object();
        //校验
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
        }
        try {
            // 检测传入的参数是否合法
            TemplateCheckUtil.checkTempleRequestDto(requestDto);

            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));

            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);

            // 加入监控
            prometheusService.incrementQueryHiveSql(sqlEntity.getTempleCode().toString(), sqlEntity.getCreator());

            result = prestoService.queryPoolTest(sql, sqlEntity.getSourceCode());
        } catch (TemplateCheckException e) {
            log.info("checkTempleRequestDtoFail", e, requestDto.toString());
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } catch (Exception e) {
            log.error("HiveDataController-queryHiveSqltest error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } finally {
            log.info("HiveDataController-queryHiveSqltest tempCode= " + requestDto.getTempleCode() + ", endTime=" + (System.currentTimeMillis() - beginTime));
        }
        return new DsapiResult(result);
    }


    /**
     * HIVE 查询SQL
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryHiveSql")
    public DsapiResult queryHiveSql(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("HiveDataController-queryBySql requestDto=" + JSON.toJSONString(requestDto));
        long beginTime = System.currentTimeMillis();
        log.info("HiveDataController-queryBySql tempCode= " + requestDto.getTempleCode() + ",beginTime=" + beginTime);
        //解析sql
        Object result = new Object();
        //校验
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
        }
        try {
            // 检测传入的参数是否合法
            TemplateCheckUtil.checkTempleRequestDto(requestDto);

            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));

            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);

            // 加入监控
            prometheusService.incrementQueryHiveSql(sqlEntity.getTempleCode().toString(), sqlEntity.getCreator());

            result = prestoService.query(sql, sqlEntity.getSourceCode());
        } catch (TemplateCheckException e) {
            log.info("checkTempleRequestDtoFail", e, requestDto.toString());
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } catch (Exception e) {
            log.error("HiveDataController-queryBySql error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } finally {
            log.info("HiveDataController-queryBySql tempCode= " + requestDto.getTempleCode() + ", endTime=" + (System.currentTimeMillis() - beginTime));
        }
        return new DsapiResult(result);
    }

    /**
     * 匹配SQL解析
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/querySqlAnalyze")
    public DsapiResult querySqlAnalyze(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("HiveDataController-querySqlAnalyze requestDto=" + JSON.toJSONString(requestDto));
        try {
            if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
                return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
            }
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            return new DsapiResult(sql);
        } catch (Exception e) {
            log.error("HiveDataController-querySqlAnalyze error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }
}

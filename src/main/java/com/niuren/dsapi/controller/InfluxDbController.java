package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.InfluxDBConfig;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.InfluxDBDao;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.DbSqlQueryDto;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * kafka 数据基础服务
 */

@RestController
@RequestMapping("/api/data")
public class InfluxDbController {
    @Autowired
    InfluxDBDao influxDBDao;
    @Autowired
    private InfluxDBConfig influxDBConfig;

    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;


    private static final Logger log = LoggerFactory.getLogger(InfluxDbController.class);
    /**
     * 查询所有的数据库
     *
     * @return
     */
    @GetMapping("/queryDatabases")
    public DsapiResult queryDatabases() {
        log.info("KafkaDataController-queryDatabases");
        List<String> list = influxDBConfig.getInfluxDB().describeDatabases();

        Map<String, String> map = null;
        List<Map<String, String>> listMap = new ArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            for (String database : list) {
                map = new HashMap();
                map.put("key", database);
                listMap.add(map);
            }
        }
        log.info("KafkaDataController-queryDatabases  end=" + JSON.toJSONString(listMap));
        return new DsapiResult(listMap);
    }

    /**
     * 查询数据库对应的表
     *
     * @return
     */
    @GetMapping("/queryTables")
    public DsapiResult queryTables(String database) {
        log.info("KafkaDataController-queryTables sql=" + JSON.toJSONString(database));
        if (StringUtil.isEmpty(database)) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        List<Map> list = new ArrayList<>();
        try {
            list = influxDBDao.queryList("SHOW MEASUREMENTS", database);
            log.info("KafkaDataController-queryTables  end=" + JSON.toJSONString(list));
        } catch (Exception e) {
            log.warn("KafkaDataController-queryTables  end=", e);
        }
        return new DsapiResult(list);
    }


    /**
     * 查询所有表字段
     * table 表名称
     * database 数据库名称
     *
     * @return
     */
    @PostMapping("/queryTableTags")
    public DsapiResult queryTableTags(@RequestBody DbSqlQueryDto queryDto) {
        log.info("KafkaDataController-queryTableTags sql=" + JSON.toJSONString(queryDto));
        if (queryDto != null && StringUtil.isEmpty(queryDto.getTable())) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }

        List<Map> list = new ArrayList<>();
        try {
            String showSql = "SHOW TAG KEYS FROM " + " \"" + queryDto.getTable() + "\"";
            list = influxDBDao.queryList(showSql, queryDto.getDataBase());
            log.info("KafkaDataController-queryTableTags  end=" + JSON.toJSONString(list));
        } catch (Exception e) {
            log.warn("KafkaDataController-queryTableTags  end=", e);
        }
        return new DsapiResult(list);
    }

    /**
     * 查询所有表字段
     * sql 表名称
     * database 数据库名称
     *
     * @return
     */
    @PostMapping("/queryInfluxBySql")
    public DsapiResult queryInfluxBySql(@RequestBody DbSqlQueryDto queryDto) {
        log.info("KafkaDataController-queryInfluxBySql sql=" + JSON.toJSONString(queryDto));
        if (queryDto != null && StringUtil.isEmpty(queryDto.getSql())) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }

        //值
        List<Map> mapValueList = new ArrayList<Map>();
        //列名称
        List<Map> mapkeyList = new ArrayList<Map>();
        try {

            mapValueList = influxDBDao.queryList(queryDto.getSql(), queryDto.getDataBase());
            Map map = null;
            if (!CollectionUtils.isEmpty(mapValueList) && mapValueList.get(0) != null) {
                Set<String> setList = mapValueList.get(0).keySet();
                for (String key : setList) {
                    map = new HashMap();
                    map.put("key", key);
                    mapkeyList.add(map);
                }
            }

        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        Map resultMap = new HashMap();
        resultMap.put("valueList", mapValueList);
        resultMap.put("keyList", mapkeyList);


        return new DsapiResult(resultMap);
    }

    /**
     * HIVE 查询SQL
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryInfluxSql")
    public DsapiResult queryInfluxSql(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("InfluxDbController-queryInfluxSql requestDto=" + JSON.toJSONString(requestDto));
        //解析sql
        Object result = new Object();
        //校验
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
        }
        List<Map> resultList = null;
        try {
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            resultList = influxDBDao.queryList(sql, sqlEntity.getSourceCode());
        } catch (Exception e) {
            log.error("InfluxDbController-queryInfluxSql error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        return new DsapiResult(resultList);
    }

}

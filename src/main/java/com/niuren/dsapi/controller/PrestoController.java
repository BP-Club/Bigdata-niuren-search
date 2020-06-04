package com.niuren.dsapi.controller;

import com.niuren.dsapi.service.PrestoService;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.service.impl.RestApiResult;
import com.niuren.dsapi.controller.OracleDataController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Hive 数据查询
 *
 * @author: DoubleLuck.Li
 * @description:
 * @create: 2018-09-06 09:50
 **/
@RestController
@RequestMapping("/api/v1.0/presto")
public class PrestoController {


    private static final Logger log = LoggerFactory.getLogger(OracleDataController.class);

    @Resource
    private PrestoService prestoService;

    /**
     * HIVE 查询SQL
     *
     * @param sql
     * @return
     */
    @PostMapping("/sql")
    public DsapiResult queryBySql(@RequestBody String sql) {
        String source = "hive01";
        Object result = prestoService.query(sql, source);
        return new DsapiResult(result);
    }

    @GetMapping("/id/{id}")
    public DsapiResult queryById(@PathVariable Long id) {
        Object result = prestoService.query(id);
        return new DsapiResult(result);
    }

}

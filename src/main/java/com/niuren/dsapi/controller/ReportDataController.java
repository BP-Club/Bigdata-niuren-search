package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.SysDataSource;
import com.niuren.dsapi.exception.TemplateCheckException;
import com.niuren.dsapi.model.Entity.ReckonRuleEntity;
import com.niuren.dsapi.model.Entity.TempleLogEntity;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dto.DataSourceDto;
import com.niuren.dsapi.service.PrestoService;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.util.AwsUploadUtil;
import com.niuren.dsapi.util.DataSourceUtil;
import com.niuren.dsapi.util.StringUtil;
import com.niuren.dsapi.util.TemplateCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * dataEye 直播间实时查询销售服务
 */
@RestController
@RequestMapping("/api/report")
public class ReportDataController {

    private static final Logger log = LoggerFactory.getLogger(ReportDataController.class);

    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    @Resource
    private PrestoService prestoService;
    @Autowired
    SysDataSource sysDataSource;


    /**
     * 查询SQL模板
     *
     * @param templeCode
     * @return
     */
    @GetMapping("/querySqlTemple")
    public DsapiResult querySqlTemple(String templeCode, HttpSession session) {

        String userName = StringUtil.getUserName(session);
        log.info("CommonDataController-querySqlTemple markDto=" + JSON.toJSONString(templeCode));
        if (StringUtils.isEmpty(templeCode)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }

        return sqlTempleOptApi.queryTempleSqlByCode(templeCode);
    }

    /**
     * 添加查询模板
     *
     * @param entity
     * @return
     */
    @PostMapping("/addSqlTemple")
    public DsapiResult addSqlTemple(@RequestBody TempleSqlEntity entity) {
        log.info("CommonDataController-addSqlTemple markDto=" + JSON.toJSONString(entity));

        if (entity == null || StringUtil.isEmpty(entity.getContent())) {
            log.warn("data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        if (!"张泰峰".equals(entity.getCreator())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, "无权操作");
        }
        entity.setCreator(entity.getCreator());
        String code = "";

        try {
            // 检测模板是否合法
            if (!"KAFKA".equals(entity.getType()) && !"REALTIME".equals(entity.getType())) {
                TemplateCheckUtil.checkSqlTemplateContent(entity.getContent());
            }

            code = sqlTempleOptApi.addSqlTempleShowCode(entity);
        } catch (TemplateCheckException e) {
            log.info("checkSqlTemplateContentFail", e, entity.getContent());
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        return new DsapiResult(code);
    }

    /**
     * 修改查询模板
     *
     * @param entity
     * @return
     */
    @PostMapping("/updateSqlTemple")
    public DsapiResult updateSqlTemple(@RequestBody TempleSqlEntity entity) {
        log.info("CommonDataController-updateSqlTemple markDto=" + JSON.toJSONString(entity));
        if (entity == null || entity.getTempleCode() == null) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        if (!"张泰峰".equals(entity.getCreator())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, "无权操作");
        }

        entity.setCreator(entity.getCreator());

        log.info("CommonDataController-updateSqlTemple userName=" + entity.getCreator());
        //检测操作合法性
        String message = sqlTempleOptApi.checkLogin(entity.getCreator(), entity.getTempleCode());
        if (StringUtil.isNotEmpty(message)) {
            return new DsapiResult(Constant.ERROR_NORULE, message);
        }

        // 检测模板是否合法
        try {
            if (!"KAFKA".equals(entity.getType()) && !"REALTIME".equals(entity.getType())) {
                TemplateCheckUtil.checkSqlTemplateContent(entity.getContent());
            }
        } catch (Exception e) {
            log.info("checkSqlTempleContentFail", e, "content:", entity.getContent());
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

        Boolean flag = sqlTempleOptApi.updateSqlTemple(entity);

        return new DsapiResult(flag);
    }


    /**
     * 查询模板列表
     *
     * @param entity
     * @return
     */
    @PostMapping("/queryTempleList")
    public DsapiResult queryTempleList(@RequestBody TempleSqlEntity entity, HttpSession session) {
        log.info("CommonDataController-queryTempleList requestDto=" + JSON.toJSONString(entity));
        try {
            entity.setCreator("张泰峰");
            List<TempleSqlEntity> list = sqlTempleOptApi.selectTempListByCon(entity);
            return new DsapiResult(list);
        } catch (Exception e) {
            log.error("CommonDataController-queryTempleList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

    /**
     * 查询数据源模板
     *
     * @param type
     * @return
     */
    @GetMapping("/queryDataSourceList")
    public DsapiResult queryDataSourceList(String type) {
        log.info("CommonDataController-queryDataSourceList requestDto=" + JSON.toJSONString(type));
        try {
            //数据源集合key
            Set<String> keySet = new HashSet<>();
            if (Constant.SourceType.ES.getCode().toString().equals(type)) {
                keySet = sysDataSource.getEsSourceMap().keySet();
            } else if (Constant.SourceType.HIVE.getCode().toString().equals(type)) {
                keySet = sysDataSource.getHiveSourceMap().keySet();
            } else if (Constant.SourceType.HBASE.getCode().toString().equals(type)) {
                keySet = sysDataSource.getHbaseSourceMap().keySet();
            } else if (Constant.SourceType.ORACLE.getCode().toString().equals(type)) {
                keySet = sysDataSource.getOracleSourceMap().keySet();
            } else if (Constant.SourceType.MYSQL.getCode().toString().equals(type)) {
                keySet = sysDataSource.getMysqlSourceMap().keySet();
            }

            List<Map<String, String>> list = new ArrayList();
            Map<String, String> keyMap = null;
            if (!CollectionUtils.isEmpty(keySet)) {
                for (String key : keySet) {
                    keyMap = new HashMap<>();
                    keyMap.put("key", key);
                    list.add(keyMap);
                }
            }


            return new DsapiResult(list);
        } catch (Exception e) {
            log.error("CommonDataController-queryDataSourceList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }


    /**
     * 查询数据源模板
     *
     * @param type
     * @return
     */
    @GetMapping("/querySourceList")
    public DsapiResult querySourceList(String type) {
        log.info("CommonDataController-querySourceList requestDto=" + JSON.toJSONString(type));
        try {
            //数据源集合key
            List<DataSourceDto> dataList = new ArrayList<>();
            if (Constant.SourceType.ES.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getEsList();
            } else if (Constant.SourceType.HIVE.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getHiveList();
            } else if (Constant.SourceType.HBASE.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getHbaseList();
            } else if (Constant.SourceType.KAFKA.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getKafkaList();
            } else if (Constant.SourceType.ORACLE.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getOracleList();
            } else if (Constant.SourceType.MYSQL.getCode().toString().equals(type)) {
                dataList = DataSourceUtil.getMysqlList();
            }

            return new DsapiResult(dataList);
        } catch (Exception e) {
            log.error("CommonDataController-queryDataSourceList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

}

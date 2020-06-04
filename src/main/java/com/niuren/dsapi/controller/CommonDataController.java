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
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/temple")
public class CommonDataController {

    private static final Logger log = LoggerFactory.getLogger(CommonDataController.class);

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
    public DsapiResult addSqlTemple(@RequestBody TempleSqlEntity entity, HttpSession session) {
        log.info("CommonDataController-addSqlTemple markDto=" + JSON.toJSONString(entity));

        if (entity == null || StringUtil.isEmpty(entity.getContent())) {
            log.warn("data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        String userName = StringUtil.getUserName(session);
        if (StringUtil.isEmpty(userName)) {
            return DsapiResult.FALSE_NOT_LOGIN;
        }
        entity.setCreator(userName);
        Boolean flag = true;

        try {
            // 检测模板是否合法
            if (!"KAFKA".equals(entity.getType()) && !"REALTIME".equals(entity.getType())) {
                TemplateCheckUtil.checkSqlTemplateContent(entity.getContent());
            }

            flag = sqlTempleOptApi.addSqlTemple(entity);
        } catch (TemplateCheckException e) {
            log.info("checkSqlTemplateContentFail", e, entity.getContent());
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        return new DsapiResult(flag);
    }

    /**
     * 修改查询模板
     *
     * @param entity
     * @return
     */
    @PostMapping("/updateSqlTemple")
    public DsapiResult updateSqlTemple(@RequestBody TempleSqlEntity entity, HttpSession session) {
        log.info("CommonDataController-updateSqlTemple markDto=" + JSON.toJSONString(entity));
        if (entity == null || entity.getTempleCode() == null) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        String userName = StringUtil.getUserName(session);
        entity.setCreator(userName);
        if (StringUtil.isEmpty(userName)) {
            return DsapiResult.FALSE_NOT_LOGIN;
        }
        log.info("CommonDataController-updateSqlTemple userName=" + userName);
        //检测操作合法性
        String message = sqlTempleOptApi.checkLogin(userName, entity.getTempleCode());
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
            String userName = StringUtil.getUserName(session);
            entity.setCreator(userName);
            //登录
            if (StringUtil.isEmpty(userName)) {
                return DsapiResult.FALSE_NOT_LOGIN;
            }
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


    /**
     * 模板下载
     *
     * @param fileName 文件名带后缀aaa.doc
     * @param response
     */
    @RequestMapping(value = "/templeDown", method = RequestMethod.GET)
    public DsapiResult templeDown(HttpServletResponse response, String filePath, String fileName) {

        if (StringUtil.isEmpty(fileName)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        if (StringUtil.isEmpty(filePath)) {
            filePath = "/libs/hive/warehouse/dsapiwork";
        }
        //下载模板
        AwsUploadUtil.awsDownloading(filePath, fileName, response);
        log.info("tempDownload-success");
        return new DsapiResult("ok");
    }

    /**
     * 模板下载
     *
     * @param tempCode 文件名带后缀aaa.doc
     */
    @GetMapping(value = "/creatRequestPagram")
    public DsapiResult creatRequestPagram(String tempCode, String type) {
        if (StringUtil.isEmpty(tempCode)) {
            log.warn("creatRequestPagram query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        Map<String, Object> result = sqlTempleOptApi.doCreateSqlParams(tempCode, type);
        log.info("creatRequestPagram-result " + JSON.toJSONString(result));
        return new DsapiResult(result);
    }


    /**
     * 添加日志记录
     *
     * @param entity
     * @return
     */
    @PostMapping("/addTempleLog")
    public DsapiResult addTempleLog(@RequestBody TempleLogEntity entity) {
        log.info("CommonDataController-addTempleLog markDto=" + JSON.toJSONString(entity));

        if (entity == null || StringUtil.isEmpty(entity.getTempleCode())) {
            log.warn("data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        Boolean flag = sqlTempleOptApi.addTempleLog(entity);

        return new DsapiResult(flag);
    }


    /**
     * 查询日志记录
     *
     * @param type
     * @param templeCode
     * @return
     */
    @GetMapping("/queryLogByCode")
    public DsapiResult queryLogByCode(String templeCode, String type) {
        log.info("CommonDataController-queryLogByCode templecode=" + templeCode + ",type=" + type);

        if (StringUtil.isEmpty(templeCode) || StringUtil.isEmpty(type)) {
            log.warn("templecode  empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        TempleLogEntity logEntity = new TempleLogEntity();
        logEntity.setType(type);
        logEntity.setTempleCode(Long.parseLong(templeCode));

        List<TempleLogEntity> list = sqlTempleOptApi.queryLogList(logEntity);

        return new DsapiResult(list);
    }


    /**
     * 添加计算任务
     *
     * @param entity
     * @return
     */
    @PostMapping("/addReckonTaskTemple")
    public DsapiResult addReckonTaskTemple(@RequestBody ReckonRuleEntity entity, HttpSession session) {
        log.info("CommonDataController-addReckonTaskTemple markDto=" + JSON.toJSONString(entity));

        if (entity == null || StringUtil.isEmpty(entity.getTempleCode())) {
            log.warn("data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        String userName = StringUtil.getUserName(session);
        if (StringUtil.isEmpty(userName)) {
            return new DsapiResult(Constant.ERROR_NORULE, "请登录后操作!");
        }
        entity.setCreator(userName);
        Boolean flag = sqlTempleOptApi.addReckonTaskTemple(entity);

        return new DsapiResult(flag);
    }

    /**
     * 查询任务列表
     *
     * @param entity
     * @return
     */
    @PostMapping("/queryReckonTaskList")
    public DsapiResult queryReckonTaskList(@RequestBody ReckonRuleEntity entity, HttpSession session) {
        log.info("CommonDataController-queryReckonTaskList requestDto=" + JSON.toJSONString(entity));
        try {
            String userName = StringUtil.getUserName(session);
            entity.setCreator(userName);
            List<ReckonRuleEntity> list = sqlTempleOptApi.queryReckonTaskListByCon(entity);
            return new DsapiResult(list);
        } catch (Exception e) {
            log.error("CommonDataController-queryReckonTaskList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

    /**
     * 查询模板列表
     *
     * @param session
     * @return
     */
    @PostMapping("/queryTopicTemList")
    public DsapiResult queryTopicTemList(HttpSession session) {
        log.info("CommonDataController-queryTopicTemList requestDto=");
        try {
            TempleSqlEntity entity = new TempleSqlEntity();
            String userName = StringUtil.getUserName(session);
            entity.setCreator(userName);
            List<Map<String, String>> mapList = sqlTempleOptApi.queryTopicTemList(entity);
            return new DsapiResult(mapList);
        } catch (Exception e) {
            log.error("CommonDataController-queryTopicTemList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
    }

    /**
     * Topic 列表
     *
     * @param templeCode
     * @return
     */
    @GetMapping("/queryTopicProperties")
    public DsapiResult queryTopicProperties(String templeCode, HttpSession session) {

        String userName = StringUtil.getUserName(session);
        log.info("CommonDataController-queryTopicProperties markDto=" + JSON.toJSONString(templeCode));
        if (StringUtils.isEmpty(templeCode)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }

        return sqlTempleOptApi.queryTopicProperties(templeCode);
    }

}

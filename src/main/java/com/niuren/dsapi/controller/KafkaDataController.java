package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.ReckonRuleDto;
import com.niuren.dsapi.model.dto.SendKafkaDataDto;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.KafkaDataQueryApi;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import com.niuren.dsapi.service.prometheus.PrometheusService;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * kafka 数据基础服务
 */

@RestController
@RequestMapping("/api/data")
public class KafkaDataController {

    private static final Logger log = LoggerFactory.getLogger(KafkaDataController.class);

    @Autowired
    private KafkaDataQueryApi kafkaDataQueryApi;

    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    PrometheusService prometheusService;

    /**
     * 1:获取topic列表
     *
     * @param kafkaType
     * @return
     */
    @GetMapping("/queryTopicList")
    public DsapiResult queryTopicList(String kafkaType) {
        log.info("KafkaDataController-queryTopicList sql=" + JSON.toJSONString(kafkaType));
        if (StringUtils.isEmpty(kafkaType)) {
            log.warn("query sql is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        return kafkaDataQueryApi.queryTopicList(kafkaType);
    }

    /**
     * 2:创建topic
     *
     * @param kafkaType
     * @return
     */
    @GetMapping("/createTopic")
    public DsapiResult createTopic(String kafkaType, String topicName) {
        log.info("KafkaDataController-createTopic sql=" + JSON.toJSONString(kafkaType));
        if (StringUtils.isEmpty(kafkaType)) {
            log.warn("query sql is empty " + Constant.EROR_MSG_NORULE);
            kafkaType = "kafka01";
        }
        return kafkaDataQueryApi.createTopic(kafkaType, topicName);
    }


    /**
     * 3.保存计算任务并通知生成任务
     *
     * @param dataDto
     * @return
     */
    @PostMapping("/addReckonTask")
    public DsapiResult addReckonTask(@RequestBody ReckonRuleDto dataDto, HttpSession session) {
        log.info("KafkaDataController-addReckonTask sql=" + JSON.toJSONString(dataDto));
        if (dataDto == null) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        String userName = StringUtil.getUserName(session);
        if (StringUtil.isEmpty(userName)) {
            return DsapiResult.FALSE_NOT_LOGIN;
        }
        dataDto.setCreator(userName);
        try {
            DsapiResult result = kafkaDataQueryApi.addReckonTask(dataDto);
            return result;
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_NORULE, e.getMessage());
        }

    }

    /**
     * 3.更新计算任务并通知生成任务
     *
     * @param dataDto
     * @return
     */
    @PostMapping("/updateReckonTask")
    public DsapiResult updateReckonTask(@RequestBody ReckonRuleDto dataDto, HttpSession session) {
        log.info("KafkaDataController-updateReckonTask sql=" + JSON.toJSONString(dataDto));
        if (dataDto == null) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        String userName = StringUtil.getUserName(session);
        if (StringUtil.isEmpty(userName)) {
            return DsapiResult.FALSE_NOT_LOGIN;
        }
        dataDto.setCreator(userName);
        try {
            DsapiResult result = kafkaDataQueryApi.updateReckonTask(dataDto);
            return result;
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_NORULE, e.getMessage());
        }
    }

    /**
     * 4.执行数据查询
     *
     * @param id
     * @return
     */
    @GetMapping("/queryReckonRule")
    public DsapiResult queryReckonRule(String id) {
        log.info("KafkaDataController-queryReckonRule sql=" + JSON.toJSONString(id));
        if (StringUtil.isEmpty(id)) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        return kafkaDataQueryApi.queryReckonRule(id);
    }

    /**
     * 4.执行数据查询
     *
     * @param tempCode
     * @return
     */
    @GetMapping("/creatRequestPagram")
    public DsapiResult creatRequestPagram(String tempCode) {
        log.info("KafkaDataController-creatRequestPagram sql=" + JSON.toJSONString(tempCode));
        if (StringUtil.isEmpty(tempCode)) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        return kafkaDataQueryApi.creatRequestPagram(tempCode);
    }


    /**
     * 1:实时数据查询SQL--对外接口-----------
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/queryRealTimeData")
    public DsapiResult queryRealTimeData(@RequestBody SqlTempleRequestDto requestDto) {
        log.info("HiveDataController-queryRealTimeSql requestDto=" + JSON.toJSONString(requestDto));
        //解析sql
        Object result = new Object();
        //校验
        if (requestDto == null || StringUtil.isEmpty(requestDto.getTempleCode())) {
            return new DsapiResult(Constant.ERROR_NORULE, "TempleCode is empty !");
        }
        try {
            //解析sql
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(requestDto.getTempleCode()));
            String sql = sqlTempleOptApi.doMatchSql(requestDto, sqlEntity);
            //执行查询
            List<Map> resultList = kafkaDataQueryApi.queryRealTimeData(sql);

            // 加入监控
            prometheusService.incrementQueryRealTimeData(sqlEntity.getTempleCode().toString(), sqlEntity.getCreator());

            return new DsapiResult(resultList);
        } catch (Exception e) {
            log.error("HiveDataController-queryBySql error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

    }


    /**
     * 2:发送数据到topic--对外接口-------------
     *
     * @param dataDto
     * @return
     */
    @PostMapping("/sendKafKaData")
    public DsapiResult sendKafkaData(@RequestBody SendKafkaDataDto dataDto) {
        log.info("KafkaDataController-sendKafkaData sql=" + JSON.toJSONString(dataDto));
        if (dataDto != null && StringUtils.isEmpty(dataDto.getTempleCode()) && !CollectionUtils.isEmpty(dataDto.getSendList())) {
            log.warn("query  is empty " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }
        return kafkaDataQueryApi.sendDataList(dataDto);
    }


    /**
     * 3:sql解析查询--对外接口
     *
     * @param sql
     * @return
     */
    @GetMapping("/queryRealTimeDataBySql")
    public DsapiResult queryRealTimeDataBySql(String sql) {

        List<Map> mapList = new ArrayList<Map>();
        try {
            if (StringUtil.isNotEmpty(sql)) {
                mapList = kafkaDataQueryApi.queryRealTimeData(sql);
            }
        } catch (Exception e) {
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }
        return new DsapiResult(mapList);
    }

    /**
     * 3:sql解析表结构
     *
     * @param sql
     * @return
     */
    @GetMapping("/queryTableBySql")
    public DsapiResult queryTableBySql(String sql) {
        //值
        List<Map> mapValueList = new ArrayList<Map>();
        //列名称
        List<Map> mapkeyList = new ArrayList<Map>();
        try {
            if (StringUtil.isNotEmpty(sql)) {
                mapValueList = kafkaDataQueryApi.queryRealTimeData(sql);
                Map map = null;
                if (!CollectionUtils.isEmpty(mapValueList) && mapValueList.get(0) != null) {
                    Set<String> setList = mapValueList.get(0).keySet();
                    for (String key : setList) {
                        map = new HashMap();
                        map.put("key", key);
                        mapkeyList.add(map);
                    }
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

}

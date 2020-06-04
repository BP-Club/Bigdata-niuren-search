package com.niuren.dsapi.service.impl;


import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceKafkaConfig;
import com.niuren.dsapi.config.InfluxDBConfig;
import com.niuren.dsapi.config.SysDataSource;
import com.niuren.dsapi.model.Entity.ReckonRuleEntity;
import com.niuren.dsapi.model.Entity.TempleLogEntity;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.InfluxDBDao;
import com.niuren.dsapi.model.dao.ReckonRuleEntityMapper;
import com.niuren.dsapi.model.dao.TempleLogEntityMapper;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.KafkaDataQueryApi;
import com.niuren.dsapi.service.prometheus.PrometheusService;
import com.niuren.dsapi.util.DateUtil;
import com.niuren.dsapi.util.EsResfulHttpUtil;
import com.niuren.dsapi.util.KafkaUtil;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaDataQueryImp implements KafkaDataQueryApi {


    private static final Logger log = LoggerFactory.getLogger(KafkaDataQueryImp.class);
    @Autowired
    InfluxDBDao influxDBDao;
    @Autowired
    private InfluxDBConfig influxDBConfig;
    @Autowired
    SysDataSource sysDataSource;

    @Autowired
    DataSourceKafkaConfig dataSourceKafkaConfig;

    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    TempleLogEntityMapper templeLogEntityMapper;

    @Autowired
    ReckonRuleEntityMapper reckonRuleEntityMapper;

    @Autowired
    private PrometheusService prometheusService;

    @Value("${realTime.goUrl}")
    private String goUrl;

    @Override
    public DsapiResult queryTopicList(String kafkaType) {
        //先找到数据源
        List<Map<String, String>> list = new ArrayList();
        KafkaUtil kafkaUtil = (KafkaUtil) sysDataSource.getKafkaSource(kafkaType);
        List<String> topicList = kafkaUtil.getTopicList();
        Map<String, String> keyMap = null;
        if (!CollectionUtils.isEmpty(topicList)) {
            for (String topic : topicList) {
                keyMap = new HashMap<>();
                keyMap.put("key", topic);
                list.add(keyMap);
            }
        }

        return new DsapiResult(list);
    }

    /**
     * 查询topic 是否存在 true 存在，false 不存在
     *
     * @param kafkaType
     * @param kafkaName
     * @return
     */
    @Override
    public boolean checkTopicIsExist(String kafkaType, String kafkaName) {
        KafkaUtil kafkaUtil = (KafkaUtil) sysDataSource.getKafkaSource(kafkaType);
        List<String> topicList = kafkaUtil.getTopicList();
        for (String topicName : topicList) {
            if (topicName.equals(kafkaName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Map> queryRealTimeData(String sql) throws Exception {
        List<Map> mapList = influxDBDao.queryList(sql, influxDBConfig.getRealTimeDatabase());
        return mapList;
    }

    @Override
    public DsapiResult createTopic(String kafkaType, String topicName) {
        KafkaUtil kafkaUtil = (KafkaUtil) sysDataSource.getKafkaSource(kafkaType);
        boolean flag = kafkaUtil.createTopic(topicName);
        return new DsapiResult(flag);
    }

    @Override
    public DsapiResult sendDataList(SendKafkaDataDto dataDto) {
        //1：根据模板查询 数据源和topic
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(dataDto.getTempleCode()));
        //获取topic
        String topic = sqlEntity.getSourceCode();
        //kafka数据源
        String dataSource = "kafka01";
        //解析数据源
        KafkaUtil kafkaUtil = (KafkaUtil) sysDataSource.getKafkaSource(dataSource);
        for (Object data : dataDto.getSendList()) {
            kafkaUtil.sendMessage(topic, JSON.toJSONString(data));
        }

        Long templeCode = sqlEntity.getTempleCode();
        String creator = sqlEntity.getCreator();
        prometheusService.incrementSendKafkaData(templeCode.toString(), creator);

        return new DsapiResult("ok");
    }

    @Override
    public DsapiResult addReckonTask(ReckonRuleDto dataDto) {
        log.info("KafkaDataQueryImp-addReckonTask-begin dataDto=" + JSON.toJSONString(dataDto));
        //1:A检测模板是否存在
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(dataDto.getTempleCode()));
        if (sqlEntity == null) {
            log.info("KafkaDataQueryImp-addReckonTask-templeCode is empty please check !");
            return new DsapiResult(Constant.ERROR_OTHER, "templeCode is empty please check !");
        }
        //1：B校验参数合法性
        String checkResult = checkSqlFormat(dataDto, sqlEntity);
        if (StringUtil.isNotEmpty(checkResult)) {
            log.warn("KafkaDataQueryImp-checkSqlFormat=" + checkResult);
            return new DsapiResult(Constant.ERROR_OTHER, checkResult);
        }
        //2:校验topic是否存在
        boolean flag = checkTopicIsExist("kafka01", sqlEntity.getSourceCode());
        if (!flag) {
            log.info("KafkaDataQueryImp-addReckonTask-topic is not exist");
            return new DsapiResult(Constant.ERROR_OTHER, "topic is not exist !");
        }
        //3:校验规则json
        try {
            ReckonRuleEntity ruleEntity = new ReckonRuleEntity();
            BeanUtils.copyProperties(dataDto, ruleEntity);
            //创建实时任务表
            String tableName = Constant.REALTIME_TASK + DateUtil.getYYYYmmddHHssSSS();
            ruleEntity.setTabaleName(tableName);

            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();

            logEntity.setType(sqlEntity.getType());
            logEntity.setOptName(dataDto.getCreator());
            logEntity.setOptStatus("01");//创建


            // 孟恒实时Json任务规则
            KafkaDto kafka = new KafkaDto();
            kafka.setUrl(dataSourceKafkaConfig.getBrokenList());
            kafka.setTableName(tableName);
            kafka.setTopic(sqlEntity.getSourceCode());
            kafka.setRule(JSON.parseObject(sqlEntity.getContent(), Map.class));
            dataDto.setKafka(kafka);

            dataDto.setName(null);
            dataDto.setTempleCode(null);
            dataDto.setTaskStatus(null);
            dataDto.setCreator(null);
            dataDto.setRuleType(null);
            dataDto.setIsDel(null);

            ruleEntity.setId(null);
            ruleEntity.setRule(JSON.toJSONString(dataDto));

            //4：通知孟恒 任务
            String url = goUrl + "/api/v1/spark/run";
            Map paramMap = new HashMap<>();
            paramMap.put("Name", ruleEntity.getTabaleName());
            paramMap.put("Param", JSON.toJSONString(JSON.toJSONString(dataDto)));

            log.info("KafkaDataQueryImp-addReckonTask-request-url=" + url);
            log.info("KafkaDataQueryImp-addReckonTask-request=" + JSON.toJSONString(paramMap));
            String response = EsResfulHttpUtil.postOrPutOrDelete(url, JSON.toJSONString(paramMap), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("KafkaDataQueryImp-addReckonTask-result=" + response);

            //3：保存规则
            reckonRuleEntityMapper.insertSelective(ruleEntity);
            log.info("KafkaDataQueryImp-addReckonTask-info=" + JSON.toJSONString(dataDto));


            //5:增加操作日志
            logEntity.setTempleCode(ruleEntity.getId().longValue());
            templeLogEntityMapper.insertSelective(logEntity);

        } catch (Exception e) {
            log.error("KafkaDataQueryImp-addReckonTask-faile ", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

        log.info("KafkaDataQueryImp-addReckonTask-end ");
        return new DsapiResult("ok");

    }

    /**
     * 检测结构是否符合规范
     *
     * @param dataDto
     * @param sqlEntity
     * @return
     */
    private String checkSqlFormat(ReckonRuleDto dataDto, TempleSqlEntity sqlEntity) {
        //sql格式模板
        String errorMsg = "";
        String jsonSql = sqlEntity.getContent();
        if (StringUtil.isNotEmpty(jsonSql)) {
            //map 解析
            Map mapData = JSON.parseObject(jsonSql, Map.class);
            List<SelectDto> selectList = dataDto.getSelect();
            if (CollectionUtils.isEmpty(selectList)) {
                errorMsg = "select 条件不能为空!";
                return errorMsg;
            }
            //校验where条件不能为空
            for (SelectDto select : selectList) {
                if (select == null || (StringUtil.isEmpty(select.getAggs()) && StringUtil.isEmpty(select.getName()) && StringUtil.isEmpty(select.getDistant()))) {
                    errorMsg = "select 条件不能为空!";
                    return errorMsg;
                }
                //校验参数的合法性
                if (StringUtil.isNotEmpty(select.getAggs()) && "sum,avg,count".contains(select.getAggs())) {
                    //如果聚合选中，则字段必须不为空且String 类型
                    if (StringUtil.isEmpty(select.getName())) {
                        errorMsg = "select 字段属性不能为空!";
                        return errorMsg;
                    } else if ("String".equals((String) mapData.get(select.getName()))) {
                        errorMsg = "select 属性为String类型不支持聚合!";
                        return errorMsg;
                    }
                }
            }

            //校验where条件不能为空
            List<WhereDto> whereList = dataDto.getWhere();
            if (!CollectionUtils.isEmpty(whereList)) {
                for (WhereDto where : whereList) {
                    //校验参数的合法性
                    if (where != null) {
                        //如果有一个不为空则属性值必须存在
                        if (StringUtil.isEmpty(where.getName()) && (StringUtil.isNotEmpty(where.getValue()) || StringUtil.isNotEmpty(where.getFunction()))) {
                            errorMsg = "where 属性不为空!";
                            return errorMsg;
                        } else if (StringUtil.isNotEmpty(where.getName()) && (StringUtil.isEmpty(where.getValue()) || StringUtil.isEmpty(where.getFunction()))) {
                            errorMsg = "where 范围 或 输入值不会空!";
                            return errorMsg;
                        }
                    }
                }
            }
            //校验分组
            List<GroupDto> groupbyList = dataDto.getGroupby();
            if (CollectionUtils.isEmpty(groupbyList)) {
                errorMsg = "group 属性不能为空!";
                return errorMsg;
            }
            for (GroupDto groupDto : groupbyList) {
                if (StringUtil.isEmpty(groupDto.getName())) {
                    errorMsg = "group 属性不能为空!";
                    return errorMsg;
                }
            }
            //汇总频率
            LimitDto limit = dataDto.getLimit();
            if (StringUtil.isEmpty(limit.getTime())) {
                errorMsg = "汇总频率 不能为空!";
                return errorMsg;
            }

        }

        return errorMsg;
    }

    @Override
    public DsapiResult updateReckonTask(ReckonRuleDto dataDto) {
        log.info("KafkaDataQueryImp-addReckonTask-begin dataDto=" + JSON.toJSONString(dataDto));
        //1A:检测模板是否存在
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(dataDto.getTempleCode()));
        if (sqlEntity == null) {
            log.info("KafkaDataQueryImp-addReckonTask-templeCode is empty please check !");
            return new DsapiResult(Constant.ERROR_OTHER, "templeCode is empty please check !");
        }
        //1B：校验参数合法性
        String checkResult = checkSqlFormat(dataDto, sqlEntity);
        if (StringUtil.isNotEmpty(checkResult)) {
            log.warn("KafkaDataQueryImp-checkSqlFormat=" + checkResult);
            return new DsapiResult(Constant.ERROR_OTHER, checkResult);
        }

        //2:校验topic是否存在
        boolean flag = checkTopicIsExist("kafka01", sqlEntity.getSourceCode());
        if (!flag) {
            log.info("KafkaDataQueryImp-addReckonTask-topic is not exist");
            return new DsapiResult(Constant.ERROR_OTHER, "topic is not exist !");
        }
        //3:校验规则json

        try {
            ReckonRuleEntity ruleEntity = new ReckonRuleEntity();
            BeanUtils.copyProperties(dataDto, ruleEntity);
            ruleEntity.setId(Integer.parseInt(dataDto.getId()));
            //创建实时任务表
            String tableName = Constant.REALTIME_TASK + DateUtil.getYYYYmmddHHssSSS();
            ruleEntity.setTabaleName(tableName);

            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();

            logEntity.setType(sqlEntity.getType());
            logEntity.setOptName(dataDto.getCreator());
            logEntity.setOptStatus("02");//修改


            // 孟恒实时Json任务规则
            KafkaDto kafka = new KafkaDto();
            kafka.setUrl(dataSourceKafkaConfig.getBrokenList());
            kafka.setTableName(tableName);
            kafka.setTopic(sqlEntity.getSourceCode());
            kafka.setRule(JSON.parseObject(sqlEntity.getContent(), Map.class));
            dataDto.setKafka(kafka);

            dataDto.setName(null);
            dataDto.setTempleCode(null);
            dataDto.setTaskStatus(null);
            dataDto.setCreator(null);
            dataDto.setRuleType(null);
            dataDto.setIsDel(null);
            dataDto.setId(null);

            ruleEntity.setRule(JSON.toJSONString(dataDto));
            //4：通知孟恒 任务
            //TODO
            //4：通知孟恒 任务
            String url = goUrl + "/api/v1/spark/run";
            Map paramMap = new HashMap<>();
            paramMap.put("Name", ruleEntity.getTabaleName());
            paramMap.put("Param", JSON.toJSONString(JSON.toJSONString(dataDto)));

            log.info("KafkaDataQueryImp-addReckonTask-request-url=" + url);
            log.info("KafkaDataQueryImp-addReckonTask-request=" + JSON.toJSONString(paramMap));
            String response = EsResfulHttpUtil.postOrPutOrDelete(url, JSON.toJSONString(paramMap), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("KafkaDataQueryImp-addReckonTask-result=" + response);
            //3：保存规则
            reckonRuleEntityMapper.updateByPrimaryKeySelective(ruleEntity);
            log.info("KafkaDataQueryImp-addReckonTask-info=" + JSON.toJSONString(dataDto));


            //5:增加操作日志
            logEntity.setTempleCode(ruleEntity.getId().longValue());
            templeLogEntityMapper.insertSelective(logEntity);

        } catch (Exception e) {
            log.error("KafkaDataQueryImp-addReckonTask-faile ", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        }

        log.info("KafkaDataQueryImp-addReckonTask-end ");
        return new DsapiResult("ok");
    }

    @Override
    public DsapiResult queryKafkaData(SqlTempleRequestDto dataDto) {
        return null;
    }

    @Override
    public DsapiResult queryReckonRule(String id) {
        ReckonRuleEntity ruleEntity = reckonRuleEntityMapper.selectByPrimaryKey(Integer.parseInt(id));
        ReckonRuleDto dataDto = new ReckonRuleDto();
        if (ruleEntity != null) {

            if (StringUtil.isNotEmpty(ruleEntity.getRule())) {
                dataDto = JSON.parseObject(ruleEntity.getRule(), dataDto.getClass());
            }

            dataDto.setIsDel(ruleEntity.getIsDel());
            dataDto.setTempleCode(ruleEntity.getTempleCode());
            dataDto.setName(ruleEntity.getName());
            dataDto.setRuleType(ruleEntity.getRuleType());
            dataDto.setTaskStatus(ruleEntity.getTaskStatus());

        }
        return new DsapiResult(dataDto);
    }

    @Override
    public DsapiResult creatRequestPagram(String tempCode) {
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(tempCode));
        String content = "";
        if (sqlEntity != null) {
            content = sqlEntity.getContent();
        }
        SendKafkaDataDto dataDto = new SendKafkaDataDto();
        dataDto.setTempleCode(tempCode);
        List<Object> sendList = new ArrayList<>();
        sendList.add(JSON.parse(content));
        sendList.add(JSON.parse(content));
        dataDto.setSendList(sendList);
        return new DsapiResult(dataDto);
    }
}

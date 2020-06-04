package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.model.Entity.ReckonRuleEntity;
import com.niuren.dsapi.model.Entity.TempleLogEntity;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dao.ReckonRuleEntityMapper;
import com.niuren.dsapi.model.dao.TempleLogEntityMapper;
import com.niuren.dsapi.model.dao.TempleSqlEntityMapper;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.model.dto.TopicPropertiesDto;
import com.niuren.dsapi.service.KafkaDataQueryApi;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.util.FreeMarkSqlUtils;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SqlTempleOptImp implements SqlTempleOptApi {

    private static final Logger log = LoggerFactory.getLogger(SqlTempleOptImp.class);
    @Autowired
    TempleSqlEntityMapper templeSqlEntityMapper;

    @Autowired
    TempleLogEntityMapper templeLogEntityMapper;
    @Autowired
    ReckonRuleEntityMapper reckonRuleEntityMapper;

    @Autowired
    private KafkaDataQueryApi kafkaDataQueryApi;


    @Override
    public String doMatchSql(SqlTempleRequestDto requestDto, TempleSqlEntity sqlEntity) {
        long beginTime = System.currentTimeMillis();
        log.info("**********doMatchSql-begin-dto= beginTime=" + beginTime);
        //1:根据templeCode查询sql 模板
        String sql = null;
        //参数-表字段{}和变量 : 例如：  sql = "select * from test where {ids}=:id and starttime=:starttime or nodeid=:nodeid";
        if (sqlEntity != null) {
            sql = sqlEntity.getContent();
        }
        if (StringUtil.isEmpty(sql)) {
            throw new ServiceException("sql templeCode can't find or sql is disabled please check templecode=" + requestDto.getTempleCode() + ",requestDto=" + JSON.toJSONString(requestDto));
        }

        log.info("**********query-sql=");


        //2:匹配所有表达式中的参数
        String newSql = "";
        try {
            Map<String, Object> params = requestDto.getParamsMap();
            //增加freemakeer解析
            sql = FreeMarkSqlUtils.getQueryString(sql, params);

            if (params != null && StringUtil.isNotEmpty(sql)) {
                //对:参数执行解析
                newSql = sql;
                Pattern pattern = Pattern.compile("\\:(\\w+)");
                Matcher matcher = pattern.matcher(sql);
                //:参数替换
                while (matcher.find()) {
                    //对值变量替换 :value
                    String groupParam = matcher.group(0);
                    String groupName = matcher.group(1);
                    if (StringUtil.isNotEmpty(groupName) && params.containsKey(groupName)) {
                        //默认字符类型增加引号查询
                        if (params.get(groupName) instanceof List) {
                            List<Object> jsonList = (List) params.get(groupName);
                            StringBuilder builder = new StringBuilder();
                            for (Object obj : jsonList) {
                                if (obj instanceof String) {
                                    builder.append(StringUtil.getInNoDbFormat((String) obj));
                                } else {
                                    builder.append(obj);
                                }
                                builder.append(",");
                            }
                            builder.deleteCharAt(builder.lastIndexOf(","));
                            newSql = newSql.replace(groupParam, builder.toString());
                        } else {
                            if (params.get(groupName) instanceof String) {//数据转换
                                newSql = newSql.replace(groupParam, StringUtil.getInNoDbFormat((String) params.get(groupName)));
                            } else {
                                newSql = newSql.replace(groupParam, StringUtil.getInNoDbFormat(Integer.toString((Integer) params.get(groupName))));
                            }
                        }
                    }
                }
                //对查询字段变量替换格式{test}
                Pattern pattern2 = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
                Matcher matcher2 = pattern2.matcher(newSql);
                while (matcher2.find()) {
                    String temp = "{" + matcher2.group() + "}";
                    if (params.containsKey(matcher2.group())) {
                        if (params.get(matcher2.group()) instanceof List) {
                            List<Object> jsonList = (List) params.get(matcher2.group());
                            StringBuilder builder = new StringBuilder();
                            for (Object obj : jsonList) {
                                builder.append(obj);
                                builder.append(",");
                            }
                            builder.deleteCharAt(builder.lastIndexOf(","));
                            newSql = newSql.replace(temp, builder.toString());
                        } else {
                            if (params.get(matcher2.group()) instanceof String) { //数据转换
                                newSql = newSql.replace(temp, (String) params.get(matcher2.group()));
                            } else {
                                newSql = newSql.replace(temp, Integer.toString((Integer) params.get(matcher2.group())));
                            }
                        }
                    }
                }
            }
            //增加freemakeer解析
            newSql = newSql.replaceAll("[\\n\\r]", " ");
        } catch (Exception e) {
            throw new ServiceException("sql error TempleCode=" + requestDto.getTempleCode() + ",requestDto=" + JSON.toJSONString(requestDto));
        } finally {
            log.info("**********doMatchSql-result-replace-end-time=" + (System.currentTimeMillis() - beginTime));
        }
        log.info("**********doMatchSql-result-replace-end-sql=" + newSql);

        return newSql;
    }

    @Override
    public DsapiResult queryTempleSqlByCode(String templeCode) {
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(templeCode));
        log.info("SqlTempleOptImp-queryTempleSqlByCode result=" + JSON.toJSONString(sqlEntity));
        return new DsapiResult(sqlEntity);
    }

    @Override
    public boolean addSqlTemple(TempleSqlEntity entity) {
        log.info("SqlTempleOptImp-addSqlTemple-begin entity=" + JSON.toJSONString(entity));


        if (Constant.SourceType.KAFKA.toString().equals(entity.getType())) {
            //创建topic前先校验是否存在
            boolean flag = kafkaDataQueryApi.checkTopicIsExist("kafka01", entity.getSourceCode());
            if (flag) {//如果存在
                TempleSqlEntity query = new TempleSqlEntity();
                query.setType(Constant.SourceType.KAFKA.toString());
                query.setSourceCode(entity.getSourceCode());
                //判断下现有模板中是否有引用了此topic
                List temList = templeSqlEntityMapper.selectTempListByCon(query);
                if (!CollectionUtils.isEmpty(temList)) {
                    throw new ServiceException("creat fail topic is exist", Constant.ERROR_OTHER);
                }
                log.warn("SqlTempleOptImp-addSqlTemple-creat topic use old is exist");
                //throw new ServiceException("creat topic is exist", Constant.ERROR_OTHER);
            } else {
                //创建topic
                DsapiResult result = kafkaDataQueryApi.createTopic("kafka01", entity.getSourceCode());
                if ((Boolean) result.get("data") == false) {
                    log.warn("SqlTempleOptImp-addSqlTemple-creat topic fail");
                    throw new ServiceException("creat topic fail", Constant.ERROR_OTHER);
                }
            }

        }
        int flag = 0;
        try {
            flag = templeSqlEntityMapper.insertSelective(entity);
            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();
            logEntity.setTempleCode(entity.getTempleCode());
            logEntity.setType(entity.getType());
            logEntity.setOptName(entity.getCreator());
            logEntity.setOptStatus("01");//新增
            addTempleLog(logEntity);
        } catch (Exception e) {
            log.info("SqlTempleOptImp-addSqlTemple fail=" + e);
        }
        if (flag > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String addSqlTempleShowCode(TempleSqlEntity entity) {
        log.info("SqlTempleOptImp-addSqlTemple-begin entity=" + JSON.toJSONString(entity));

        if (Constant.SourceType.KAFKA.toString().equals(entity.getType())) {
            //创建topic前先校验是否存在
            boolean flag = kafkaDataQueryApi.checkTopicIsExist("kafka01", entity.getSourceCode());
            if (flag) {//如果存在
                TempleSqlEntity query = new TempleSqlEntity();
                query.setType(Constant.SourceType.KAFKA.toString());
                query.setSourceCode(entity.getSourceCode());
                //判断下现有模板中是否有引用了此topic
                List temList = templeSqlEntityMapper.selectTempListByCon(query);
                if (!CollectionUtils.isEmpty(temList)) {
                    throw new ServiceException("creat fail topic is exist", Constant.ERROR_OTHER);
                }
                log.warn("SqlTempleOptImp-addSqlTemple-creat topic use old is exist");
                //throw new ServiceException("creat topic is exist", Constant.ERROR_OTHER);
            } else {
                //创建topic
                DsapiResult result = kafkaDataQueryApi.createTopic("kafka01", entity.getSourceCode());
                if ((Boolean) result.get("data") == false) {
                    log.warn("SqlTempleOptImp-addSqlTemple-creat topic fail");
                    throw new ServiceException("creat topic fail", Constant.ERROR_OTHER);
                }
            }

        }
        int flag = 0;
        try {
            flag = templeSqlEntityMapper.insertSelective(entity);
            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();
            logEntity.setTempleCode(entity.getTempleCode());
            logEntity.setType(entity.getType());
            logEntity.setOptName(entity.getCreator());
            logEntity.setOptStatus("01");//新增
            addTempleLog(logEntity);
        } catch (Exception e) {
            log.info("SqlTempleOptImp-addSqlTemple fail=" + e);
        }
        return Long.toString(entity.getTempleCode());
    }

    @Override
    public boolean addTempleLog(TempleLogEntity entity) {
        log.info("SqlTempleOptImp-addTempleLog-begin entity=" + JSON.toJSONString(entity));
        int flag = 0;
        try {
            flag = templeLogEntityMapper.insertSelective(entity);
        } catch (Exception e) {
            log.info("SqlTempleOptImp-addSqlTemple fail=" + e);
        }

        if (flag > 0) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public List<TempleLogEntity> queryLogList(TempleLogEntity entity) {
        log.info("SqlTempleOptImp-queryLogList-begin entity=" + JSON.toJSONString(entity));
        List<TempleLogEntity> dataList = null;
        try {
            dataList = templeLogEntityMapper.selectListByTempleCode(entity);
        } catch (Exception e) {
            log.error("queryLogList error:" + e);
        }
        return dataList;
    }

    @Override
    public boolean updateSqlTemple(TempleSqlEntity entity) {
        log.info("SqlTempleOptImp-updateSqlTemple-begin entity=" + JSON.toJSONString(entity));
        int flag = 0;
        try {
            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();
            logEntity.setTempleCode(entity.getTempleCode());
            logEntity.setType(entity.getType());
            logEntity.setOptName(entity.getCreator());
            logEntity.setOptStatus("02");//修改

            entity.setCreator(null);//创建人置空
            flag = templeSqlEntityMapper.updateByPrimaryKeySelective(entity);

            addTempleLog(logEntity);
        } catch (Exception e) {
            log.info("SqlTempleOptImp-updateSqlTemple fail=" + e);
        }
        if (flag > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addSqlTemLog(TempleLogEntity entity) {
        log.info("SqlTempleOptImp-addSqlTemLog-begin entity=" + JSON.toJSONString(entity));
        int flag = 0;
        try {
            flag = templeLogEntityMapper.insert(entity);
        } catch (Exception e) {
            log.info("SqlTempleOptImp-addSqlTemLog fail=" + e);
        }
        if (flag > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<TempleSqlEntity> selectTempListByCon(TempleSqlEntity entity) {
        List<TempleSqlEntity> list = new ArrayList<>();
        if (entity != null) {
            //模糊查询描述
            if (StringUtil.isNotEmpty(entity.getDescr())) {
                entity.setDescr("%" + entity.getDescr() + "%");
            }
            list = templeSqlEntityMapper.selectTempListByCon(entity);
        }
        return list;
    }


    @Override
    public String checkLogin(String userName, Long templeCode) {
        log.info("userName=" + userName + ",templeCode=" + templeCode);
        String message = null;
        if (StringUtil.isEmpty(userName)) {
            message = "请登录后操作!";
        } else if (StringUtil.isEmpty(templeCode)) {
            message = "模板编号不能为空!";
        } else {
            //检查是否有权操作
            TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(templeCode);
            log.info("userName=" + userName + ",sqlEntity=" + JSON.toJSONString(sqlEntity));
            if (!"admin".equals(userName) && !userName.equals(sqlEntity.getCreator())) {

                message = "无权操作!";
                log.info("userName=" + userName + ",creator=" + sqlEntity.getCreator());
            }
        }


        return message;
    }


    /**
     * @param templeCode
     * @param type
     * @return
     */
    public Map<String, Object> doCreateSqlParams(String templeCode, String type) {
        log.info("**********doMatchSql-begin-dto=" + JSON.toJSONString(templeCode));
        //1:根据templeCode查询sql 模板
        String sql = null;
        //参数-表字段{}和变量 : 例如：  sql = "select * from test where {ids}=:id and starttime=:starttime or nodeid=:nodeid";
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(templeCode));
        if (StringUtil.isEmpty(sqlEntity.getContent())) {
            throw new ServiceException("sql templeCode can't find or sql is disabled please check templecode=" + templeCode);
        }
        sql = sqlEntity.getContent();
        log.info("**********query-sql=" + sql);
        //2:匹配所有表达式中的参数
        String newSql = "";
        Map<String, String> paramsMap = new HashMap<>();
        try {
            if (StringUtil.isNotEmpty(sql)) {
                //对:参数执行解析
                newSql = sql;
                Pattern pattern = Pattern.compile("\\:(\\w+)");
                Matcher matcher = pattern.matcher(sql);
                //:参数替换
                while (matcher.find()) {
                    //对值变量替换 :value
                    String groupParam = matcher.group(0);//*******use*******
                    String groupName = matcher.group(1);
                    paramsMap.put(groupName, "your vaule");

                }
                //对查询字段变量替换格式{test}
                Pattern pattern2 = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
                Matcher matcher2 = pattern2.matcher(newSql);
                while (matcher2.find()) {
                    paramsMap.put(matcher2.group(), "your vaule");
                }
            }
        } catch (Exception e) {
            throw new ServiceException("sql error TempleCode=" + templeCode);
        }
        log.info("**********doMatchSql-result-replace-end-sql=" + newSql);
        Map<String, Object> dataRes = new HashMap();
        dataRes.put("paramsMap", paramsMap);
        dataRes.put("templeCode", templeCode);
        dataRes.put("type", type);
        return dataRes;
    }


    @Override
    public boolean addReckonTaskTemple(ReckonRuleEntity entity) {
        log.info("SqlTempleOptImp-addReckonTaskTemple-begin entity=" + JSON.toJSONString(entity));
        int flag = 0;
        try {
            //插入数据，
            reckonRuleEntityMapper.insertSelective(entity);
            //执行插入日志
            TempleLogEntity logEntity = new TempleLogEntity();
            logEntity.setTempleCode((long) entity.getId());
            logEntity.setType(Constant.SourceType.REALTIME.getCode().toString());//实时计算任务
            logEntity.setOptName(entity.getCreator());
            logEntity.setOptStatus("01");//添加
            addTempleLog(logEntity);
        } catch (Exception e) {
            log.error("SqlTempleOptImp-addReckonTaskTemple fail", e);
        }
        return true;
    }

    @Override
    public List<ReckonRuleEntity> queryReckonTaskListByCon(ReckonRuleEntity entity) {
        log.info("SqlTempleOptImp-queryReckonTaskListByCon-begin entity=" + JSON.toJSONString(entity));
        List<ReckonRuleEntity> list = new ArrayList<>();
        try {
            list = reckonRuleEntityMapper.queryReckonTaskListByCon(entity);
        } catch (Exception e) {
            log.error("SqlTempleOptImp-addReckonTaskTemple fail", e);
        }
        return list;
    }


    @Override
    public List<Map<String, String>> queryTopicTemList(TempleSqlEntity entity) {
        entity.setType(Constant.SourceType.KAFKA.getCode().toString());
        List<TempleSqlEntity> listTemple = selectTempListByCon(entity);
        List<Map<String, String>> list = new ArrayList();
        Map<String, String> map = null;
        if (!CollectionUtils.isEmpty(listTemple)) {
            for (TempleSqlEntity sqlEntity : listTemple) {
                map = new HashMap<>();
                map.put("key", Long.toString(sqlEntity.getTempleCode()));
                map.put("value", sqlEntity.getDescr());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, String>> queryBdApiTopicCodeList(TempleSqlEntity entity) {
        entity.setType(Constant.SourceType.KAFKA.getCode().toString());
        List<TempleSqlEntity> listTemple = selectTempListByCon(entity);
        List<Map<String, String>> list = new ArrayList();
        Map<String, String> map = null;
        if (!CollectionUtils.isEmpty(listTemple)) {
            for (TempleSqlEntity sqlEntity : listTemple) {
                map = new HashMap<>();
                map.put("key", Long.toString(sqlEntity.getTempleCode()));
                map.put("value", sqlEntity.getSourceCode());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public DsapiResult queryTopicProperties(String templeCode) {
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(templeCode));
        if (StringUtil.isEmpty(sqlEntity.getContent())) {
            return new DsapiResult(new ArrayList<>());
        }
        String templeContent = sqlEntity.getContent();
        Map map = new HashMap();
        map = JSON.parseObject(templeContent, map.getClass());
        Set<String> keySet = null;
        List<Map<String, String>> list = new ArrayList();
        if (map != null) {
            keySet = map.keySet();
            for (String keys : keySet) {
                map = new HashMap<>();
                map.put("key", keys);
                list.add(map);
            }
        }

        return new DsapiResult(list);
    }

    @Override
    public List<Map<String, String>> queryBdApiTopicProperties(String templeCode) {
        TempleSqlEntity sqlEntity = templeSqlEntityMapper.selectByPrimaryKey(Long.parseLong(templeCode));
        List<Map<String, String>> mapList = new ArrayList();
        if (StringUtil.isEmpty(sqlEntity.getContent())) {
            return mapList;
        }
        String templeContent = sqlEntity.getContent();
        Map map = new HashMap();
        map = JSON.parseObject(templeContent, map.getClass());
        if (map != null) {
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            TopicPropertiesDto topicDto = null;
            for (Map.Entry<String, String> entry : entrySet) {
                map = new HashMap<>();
                topicDto = new TopicPropertiesDto();
                topicDto.setName(entry.getKey());
                topicDto.setType(entry.getValue());
                map.put("key", topicDto);
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public List<Map<String, String>> queryBdApiTopicTemList(TempleSqlEntity entity) {
        //查询自己创建的或者公开的topic模板
        entity.setType(Constant.SourceType.KAFKA.getCode().toString());
        entity.setIsOpen("02");//01私有，02公开
        List<TempleSqlEntity> listTemple = templeSqlEntityMapper.selectTempListByCon(entity);
        List<Map<String, String>> listMap = new ArrayList();
        Map<String, String> map = null;
        if (!CollectionUtils.isEmpty(listTemple)) {
            for (TempleSqlEntity sqlEntity : listTemple) {
                map = new HashMap<>();
                map.put("key", Long.toString(sqlEntity.getTempleCode()));
                map.put("value", sqlEntity.getDescr());
                listMap.add(map);
            }
        }
        return listMap;
    }
}

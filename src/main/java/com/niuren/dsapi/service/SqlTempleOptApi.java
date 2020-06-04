package com.niuren.dsapi.service;

import com.niuren.dsapi.model.Entity.ReckonRuleEntity;
import com.niuren.dsapi.model.Entity.TempleLogEntity;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.impl.DsapiResult;

import java.util.List;
import java.util.Map;

public interface SqlTempleOptApi {

    /*
     * 匹配Sql参数
     *
     * @param saleDto
     * @return
     */
    public String doMatchSql(SqlTempleRequestDto requestDto, TempleSqlEntity sqlEntity);


    /**
     * 根据sql模板生成 入参
     *
     * @param templeCode
     * @param type
     * @return
     */
    public Map<String, Object> doCreateSqlParams(String templeCode, String type);


    /**
     * 根据code 查询模板
     *
     * @param templeCode
     * @return
     */
    public DsapiResult queryTempleSqlByCode(String templeCode);


    /**
     * 插入模板
     *
     * @param entity
     * @return
     */
    public boolean addSqlTemple(TempleSqlEntity entity);

    /**
     * 插入模板并返回ID
     *
     * @param entity
     * @return
     */
    public String addSqlTempleShowCode(TempleSqlEntity entity);

    /**
     * 插入模板
     *
     * @param entity
     * @return
     */
    public boolean addTempleLog(TempleLogEntity entity);

    /**
     * 插入模板
     *
     * @param entity
     * @return
     */
    public List<TempleLogEntity> queryLogList(TempleLogEntity entity);

    /**
     * 更新模板
     *
     * @param entity
     * @return
     */
    public boolean updateSqlTemple(TempleSqlEntity entity);


    /**
     * 增加操作日志
     *
     * @param entity
     * @return
     */
    public boolean addSqlTemLog(TempleLogEntity entity);


    /**
     * 模糊查询模板列表
     *
     * @param entity
     * @return
     */
    public List<TempleSqlEntity> selectTempListByCon(TempleSqlEntity entity);

    /**
     * 查询可用Topic模板
     *
     * @param entity
     * @return
     */
    public List<Map<String, String>> queryTopicTemList(TempleSqlEntity entity);

    /**
     * 增加登录检查
     *
     * @param templeCode
     * @param userName
     * @return
     */
    public String checkLogin(String userName, Long templeCode);

    /**
     * 插入计算规则
     *
     * @param entity
     * @return
     */
    public boolean addReckonTaskTemple(ReckonRuleEntity entity);

    /**
     * 查询任务列表
     *
     * @param entity
     * @return
     */
    public List<ReckonRuleEntity> queryReckonTaskListByCon(ReckonRuleEntity entity);


    /**
     * 根据code Topic字段
     *
     * @param templeCode
     * @return
     */
    public DsapiResult queryTopicProperties(String templeCode);

    /**
     * 根据templeCode 查询topic对应的字段属性
     *
     * @param templeCode
     * @return
     */
    public List<Map<String, String>> queryBdApiTopicProperties(String templeCode);

    /**
     * 查询对应用户信息-自己创建的topic和别人创建公开的
     *
     * @param entity
     * @return
     */
    public List<Map<String, String>> queryBdApiTopicTemList(TempleSqlEntity entity);

    /**
     * 查询对应用户信息
     *
     * @param entity
     * @return
     */
    public List<Map<String, String>> queryBdApiTopicCodeList(TempleSqlEntity entity);
}

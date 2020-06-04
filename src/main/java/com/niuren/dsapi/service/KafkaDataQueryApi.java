package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.ReckonRuleDto;
import com.niuren.dsapi.model.dto.SendKafkaDataDto;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import com.niuren.dsapi.service.impl.DsapiResult;

import java.util.List;
import java.util.Map;

public interface KafkaDataQueryApi {
    /**
     * 1.查询topic列表
     *
     * @param kafkaType
     * @return
     */
    public DsapiResult queryTopicList(String kafkaType);

    /**
     * 2. 创建topic
     *
     * @param kafkaType
     * @return
     */
    public DsapiResult createTopic(String kafkaType, String topicName);

    /**
     * 3. 发送数据
     *
     * @param dataDto
     * @return
     */
    public DsapiResult sendDataList(SendKafkaDataDto dataDto);

    /**
     * 4. 添加数据
     *
     * @param dataDto
     * @return
     */
    public DsapiResult addReckonTask(ReckonRuleDto dataDto);

    /**
     * 4. 添加数据
     *
     * @param dataDto
     * @return
     */
    public DsapiResult updateReckonTask(ReckonRuleDto dataDto);

    /**
     * 5. 查询数据
     *
     * @param dataDto
     * @return
     */
    public DsapiResult queryKafkaData(SqlTempleRequestDto dataDto);

    /**
     * 5. 查询数据
     *
     * @param id
     * @return
     */
    public DsapiResult queryReckonRule(String id);

    /**
     * 6. 查询数据
     *
     * @param tempCode
     * @return
     */
    public DsapiResult creatRequestPagram(String tempCode);

    /**
     * 查询topic 是否存在 true 存在，false 不存在
     *
     * @param kafkaType
     * @param kafkaName
     * @return
     */
    public boolean checkTopicIsExist(String kafkaType, String kafkaName);

    /**
     * 查询实时数据influxdb
     *
     * @param sql
     * @return
     */
    public List<Map> queryRealTimeData(String sql) throws Exception;



}

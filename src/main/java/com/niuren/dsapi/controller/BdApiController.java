package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import com.niuren.dsapi.service.SqlTempleOptApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户量趋势查询接口
 */

@RestController
@RequestMapping("/bdapi/data")
public class BdApiController {

    private static final Logger log = LoggerFactory.getLogger(BdApiController.class);

    @Autowired
    SqlTempleOptApi sqlTempleOptApi;

    /**
     * 查询根据当前用户查询模板列表:key 10034, value 订单信息中文
     *
     * @param
     * @return
     */
    @GetMapping("/queryTopicTemList")
    public DsapiResult queryTopicTemList(String userName) {
        try {
            log.info("BdApiController-queryTopicTemList begin");
            TempleSqlEntity entity = new TempleSqlEntity();
            entity.setCreator(userName);
            List<Map<String, String>> entityList = sqlTempleOptApi.queryBdApiTopicTemList(entity);
            log.info("BdApiController-queryTopicTemList end");
            return new DsapiResult(entityList);
        } catch (Exception e) {
            log.error("BdApiController-queryTopicTemList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, "其他错误");
        }
    }


    /**
     * 查询根据当前用户查询模板列表:key 10034, value topic
     *
     * @param
     * @return
     */
    @GetMapping("/queryTopicCodeList")
    public DsapiResult queryTopicCodeList(String userName) {
        try {
            log.info("BdApiController-queryTopicCodeList begin");
            TempleSqlEntity entity = new TempleSqlEntity();
            entity.setCreator(userName);
            List<Map<String, String>> entityList = sqlTempleOptApi.queryBdApiTopicCodeList(entity);
            log.info("BdApiController-queryTopicCodeList end");
            return new DsapiResult(entityList);
        } catch (Exception e) {
            log.error("BdApiController-queryTopicCodeList error=", e);
            return new DsapiResult(Constant.ERROR_OTHER, "其他错误");
        }
    }


    /**
     * 根据topic模板查询对应字段属性
     *
     * @param templeCode
     * @return
     */
    @GetMapping("/queryTopicProperties")
    public DsapiResult queryTopicProperties(String templeCode) {
        log.info("BdApiController-queryTopicProperties templeCode=" + JSON.toJSONString(templeCode));
        String userName = "";
        if (StringUtils.isEmpty(templeCode)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_OTHER, Constant.EROR_MSG_NORULE);
        }
        List<Map<String, String>> entityList = sqlTempleOptApi.queryBdApiTopicProperties(templeCode);
        return new DsapiResult(entityList);
    }


}

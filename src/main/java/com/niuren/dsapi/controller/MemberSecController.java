package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.MemSecQueryApi;
import com.niuren.dsapi.service.MemSecQueryResfApi;
import com.niuren.dsapi.service.impl.RestApiResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 大会员-2期
 */
@RestController
@RequestMapping("/api/bm/member")
public class MemberSecController {

    private static final Logger log = LoggerFactory.getLogger(MemberSecController.class);
    @Resource
    MemSecQueryApi memSecQueryApi;

    @Resource
    MemSecQueryResfApi memSecQueryResfApi;


    /**
     * 用户量趋势查询
     *
     * @param numtrendDto
     * @return
     */
    @PostMapping("/numtrend/rgst")
    public RestApiResult queryNumtrendRgst(@RequestBody com.niuren.dsapi.model.dto.NumtrendRgstDto numtrendDto) {
        log.info("MemberSecController-queryNumtrendRgst conver=" + JSON.toJSONString(numtrendDto));
        if (numtrendDto == null || StringUtils.isEmpty(numtrendDto.getBrand()) || StringUtils.isEmpty(numtrendDto.getDate_type()) || StringUtils.isEmpty(numtrendDto.getStat_date())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryNumtrendRgst(numtrendDto);
    }


    /**
     * 今日首单转化率查询
     *
     * @param frstorderDto
     * @return
     */
    @PostMapping("/conversion/frstorder")
    public RestApiResult queryConverFrstorder(@RequestBody com.niuren.dsapi.model.dto.FrstorderDto frstorderDto) {
        log.info("MemberSecController-queryConverFrstorder conver=" + JSON.toJSONString(frstorderDto));
        if (frstorderDto == null || StringUtils.isEmpty(frstorderDto.getBrand())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryFrstorder(frstorderDto);
    }


    /**
     * 首单(新客)趋势查询
     *
     * @param numtrendDto
     * @return
     */
    @PostMapping("/numtrend/frstorder")
    public RestApiResult queryNumtrendFrstorder(@RequestBody com.niuren.dsapi.model.dto.NumtrendRgstDto numtrendDto) {
        log.info("MemberSecController-queryNumtrendFrstorder conver=" + JSON.toJSONString(numtrendDto));
        if (numtrendDto == null || StringUtils.isEmpty(numtrendDto.getBrand()) || StringUtils.isEmpty(numtrendDto.getDate_type()) || StringUtils.isEmpty(numtrendDto.getStat_date())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryNumtrendFrstorder(numtrendDto);
    }

    /**
     * 成交用户查询接口
     *
     * @param payuser
     * @return
     */
    @PostMapping("/numtrend/payuser")
    public RestApiResult queryNumtrendPayuser(@RequestBody com.niuren.dsapi.model.dto.NumtrendRgstDto payuser) {
        log.info("MemberSecController-queryNumtrendPayuser conver=" + JSON.toJSONString(payuser));
        if (payuser == null || StringUtils.isEmpty(payuser.getBrand()) || StringUtils.isEmpty(payuser.getDate_type()) || StringUtils.isEmpty(payuser.getStat_date())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryNumtrendPayuser(payuser);
    }

    /**
     * 今日平均单价查询接口
     *
     * @param avgPrice
     * @return
     */
    @PostMapping("/avgprice")
    public RestApiResult queryAvgPrice(@RequestBody com.niuren.dsapi.model.dto.FrstorderDto avgPrice) {
        log.info("MemberSecController-queryAvgPrice conver=" + JSON.toJSONString(avgPrice));
        if (avgPrice == null || StringUtils.isEmpty(avgPrice.getBrand())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryAvgPrice(avgPrice);
    }

    /**
     * 今日平均单价查询接口
     *
     * @param requstDto
     * @return
     */
    @PostMapping("/sendPushTask")
    public RestApiResult sendPushTask(@RequestBody com.niuren.dsapi.model.dto.PushTaskRequestDto requstDto) {
        log.info("MemberSecController-sendPushTask conver=" + JSON.toJSONString(requstDto));
        if (requstDto == null || StringUtils.isEmpty(requstDto.getTask_id())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.doPushTaskCheck(requstDto);
    }

    /**
     * push点击查询
     *
     * @param requstDto
     * @return
     */
    @PostMapping("/queryPushPlan")
    public RestApiResult queryPushPlan(@RequestBody com.niuren.dsapi.model.dto.PushPlanRequestDto requstDto) {
        log.info("MemberSecController-queryPushPlan requstDto=" + JSON.toJSONString(requstDto));
        if (requstDto == null || StringUtils.isEmpty(requstDto.getPlanId()) || StringUtils.isEmpty(requstDto.getFlag())) {
            log.warn("query data empty or not right rule:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryPushPlan(requstDto);
    }
}

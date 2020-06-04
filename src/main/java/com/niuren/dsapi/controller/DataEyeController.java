package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.InfluxDBConfig;
import com.niuren.dsapi.model.dto.DataEyeRequestDto;
import com.niuren.dsapi.service.DataEyeQueryApi;
import com.niuren.dsapi.service.impl.DsapiResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * dataEye 直播间实时查询销售服务
 */
@RestController
@RequestMapping("/api/data")
public class DataEyeController {
    private static final Logger log = LoggerFactory.getLogger(DataEyeController.class);
    @Autowired
    DataEyeQueryApi dataEyeQueryApi;

    /**
     * 直播间实时查询销售
     *
     * @param saleDto
     * @return
     */
    @PostMapping("/querySaleData")
    public DsapiResult querySaleData(@RequestBody DataEyeRequestDto saleDto) {
        log.info("DataEyeController-querySaleData markDto=" + JSON.toJSONString(saleDto));
        if (saleDto != null && StringUtils.isEmpty(saleDto.getDate())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NORULE);
            return new DsapiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE);
        }

        return dataEyeQueryApi.querySaleData(saleDto);
    }

}

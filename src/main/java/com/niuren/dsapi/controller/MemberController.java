package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.MemGroupOptApi;
import com.niuren.dsapi.service.MemSecQueryResfApi;
import com.niuren.dsapi.service.impl.RestApiResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 大数据查询接口1期
 */

@RestController
@RequestMapping("/api/bm")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    @Autowired
    MemGroupOptApi memGroupOptApi;

    @Resource
    MemSecQueryResfApi memSecQueryResfApi;

    /**
     * 会员分群查询接口
     *
     * @param markDto
     * @return
     */
    @PostMapping("/crowd/query")
    public RestApiResult queryCrowd(@RequestBody MemMarketDto markDto) {
        log.info("MemberController-queryCrowd markDto=" + JSON.toJSONString(markDto));
        //参数校验
        if (markDto == null || StringUtils.isEmpty(markDto.getBrand())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.queryCrowd(markDto);
    }


    /**
     * 会员分群查询接口
     *
     * @param exportDto
     * @return
     */
    @PostMapping("/crowd/export")
    public RestApiResult crowdExport(@RequestBody CrowdExportRequestDto exportDto) {
        log.info("MemberController-queryCrowd markDto=" + JSON.toJSONString(exportDto));
        //参数校验
        if (exportDto == null || StringUtils.isEmpty(exportDto.getBrand())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.crowdExport(exportDto);
    }

    /**
     * 人群包状态查询接口
     *
     * @param peopleGroupDto
     * @return
     */
    @PostMapping("/crowd/status")
    public RestApiResult queryStatus(@RequestBody PeopleGroupDto peopleGroupDto) {
        log.info("MemberController-queryStatus-begin markDto=" + JSON.toJSONString(peopleGroupDto));
        //参数校验
        if (StringUtils.isEmpty(peopleGroupDto)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        log.info("MemberController-queryStatus-end");
        return memSecQueryResfApi.queryStatus(peopleGroupDto);
    }

    /**
     * 会员列表查询
     *
     * @param memQueryDto
     * @return
     */
    @PostMapping("/member/query")
    public RestApiResult queryMemList(@RequestBody MemListRequestDto memQueryDto) {
        log.info("MemberController-queryMemList-begin markDto=" + JSON.toJSONString(memQueryDto));
        //参数校验
        if (StringUtils.isEmpty(memQueryDto)) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        log.info("MemberController-queryMemList-end");
        return memSecQueryResfApi.queryMemList(memQueryDto);
    }


    /**
     * 活动数据转化追踪数据查询
     *
     * @param conver
     * @return
     */
    @PostMapping("/crowd/conversion")
    public RestApiResult conversion(@RequestBody ConversionDto conver) {
        log.info("MemberController-contribution conver=" + JSON.toJSONString(conver));
        if (conver == null || StringUtils.isEmpty(conver.getPlanId())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.conversion(conver);
    }

    /**
     * 会员贡献查询
     *
     * @param contribution
     * @return
     */
    @PostMapping("/member/contribution")
    public RestApiResult contribution(@RequestBody ContributionDto contribution) {
        log.info("MemberController-contribution contribution=" + JSON.toJSONString(contribution));
        if (contribution == null || StringUtils.isEmpty(contribution.getMarket_id())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.contribution(contribution);
    }

    /**
     * 会员等级分布查询
     *
     * @param markDto
     * @return
     */
    @PostMapping("/member/level")
    public RestApiResult queryLevel(@RequestBody MemMarketDto markDto) {
        log.info("MemberController-querylevel markDto=" + JSON.toJSONString(markDto));
        if (markDto == null || StringUtils.isEmpty(markDto.getMarket_id())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.querylevel(markDto);
    }

    /**
     * 用户量趋势查询
     *
     * @param contribution
     * @return
     */
    @PostMapping("/member/numtrend")
    public RestApiResult queryNumtrend(@RequestBody ContributionDto contribution) {
        log.info("MemberController-querylevel markDto=" + JSON.toJSONString(contribution));
        if (contribution == null || StringUtils.isEmpty(contribution.getMarket_id())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memGroupOptApi.queryNumtrend(contribution);
    }


    /**
     * 会员分群按照站点查询
     *
     * @param sitDto
     * @return
     */
    @PostMapping("/crowd/siteuid")
    public RestApiResult querySiteuid(@RequestBody CrowdSiteUidReqDto sitDto) {
        log.info("MemberController-querySiteuid markDto=" + JSON.toJSONString(sitDto));
        if (sitDto != null && CollectionUtils.isEmpty(sitDto.getSite_uid())) {
            log.warn("query data empty or not right rule " + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NOFIND, Constant.EROR_MSG_NOFIND, new ArrayList(), null);
        }
        return memSecQueryResfApi.querySiteuid(sitDto);
    }

}

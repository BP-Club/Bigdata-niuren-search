package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.impl.RestApiResult;

public interface MemSecQueryResfApi {


    /**
     * 1.用户量的趋势查询接口
     *
     * @param numtrendDto
     * @return
     */
    public RestApiResult queryNumtrendRgst(NumtrendRgstDto numtrendDto);

    /**
     * 2.今日首单转换率
     *
     * @param frstorderDto
     * @return
     */
    public RestApiResult queryFrstorder(FrstorderDto frstorderDto);


    /**
     * 4.活动数据转化追踪数据查询
     *
     * @param numtrendDto
     * @return
     */
    public RestApiResult queryNumtrendFrstorder(NumtrendRgstDto numtrendDto);


    /**
     * 5.成交用户查询
     *
     * @param payuser
     * @return
     */
    public RestApiResult queryNumtrendPayuser(NumtrendRgstDto payuser);

    /**
     * 6.今日平均单价查询接口
     *
     * @param avgPrice
     * @return
     */
    public RestApiResult queryAvgPrice(FrstorderDto avgPrice);

    /**
     * 7.会员分群按照站点查询
     *
     * @param sitDto
     * @return
     */
    public RestApiResult querySiteuid(CrowdSiteUidReqDto sitDto);

    /**
     * 8.会员分群查询接口
     *
     * @param markDto
     * @return
     */
    public RestApiResult queryCrowd(MemMarketDto markDto);


    /**
     * 9.会员分群导出接口
     *
     * @param exportDto
     * @return
     */
    public RestApiResult crowdExport(CrowdExportRequestDto exportDto);

    /**
     * 10.会员列表查询接口
     *
     * @param memQueryDto
     * @return
     */
    public RestApiResult queryMemList(MemListRequestDto memQueryDto);

    /**
     * 11.人群包状态查询-EDM
     *
     * @param peopleGroupDto
     * @return
     */
    public RestApiResult queryStatus(PeopleGroupDto peopleGroupDto);

    /**
     * 12.人群包状态查询-EDM
     *
     * @param conver
     * @return
     */
    public RestApiResult conversion(ConversionDto conver);

    /**
     * 13.会员贡献查询接口
     *
     * @param contribution
     * @return
     */
    public RestApiResult contribution(ContributionDto contribution);


    /**
     * 14.会员等级分布查询
     *
     * @param markDto
     * @return
     */
    public RestApiResult querylevel(MemMarketDto markDto);


    /**
     * 15.push task 校验
     *
     * @param requstDto
     * @return
     */
    public RestApiResult doPushTaskCheck(PushTaskRequestDto requstDto);

    /**
     * 15.点击次数
     *
     * @param requstDto
     * @return
     */
    public RestApiResult queryPushPlan(PushPlanRequestDto requstDto);

}

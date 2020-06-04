package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.impl.RestApiResult;

import java.util.List;

public interface MemGroupOptApi {


    /**
     * 1.会员分群查询接口
     *
     * @param markDto
     * @return
     */
    public RestApiResult query(MemMarketDto markDto);


    /**
     * 2.人群包状态查询-EDM
     *
     * @param peopleGroupDto
     * @return
     */
    public RestApiResult queryStatus(PeopleGroupDto peopleGroupDto);

    /**
     * 3.会员列表查询接口
     *
     * @param memQueryDto
     * @return
     */
    public RestApiResult queryMemList(MemListQueryDto memQueryDto);


    /**
     * 4.活动数据-转化追踪数据
     *
     * @param conver
     * @return
     */
    public RestApiResult conversion(ConversionDto conver);


    /**
     * 会员贡献查询接口
     *
     * @param contribution
     * @return
     */
    public RestApiResult contribution(ContributionDto contribution);

    /**
     * 会员等级分布查询
     *
     * @param markDto
     * @return
     */
    public RestApiResult querylevel(MemMarketDto markDto);

    /**
     * 用户量趋势查询
     *
     * @param contribution
     * @return
     */
    public RestApiResult queryNumtrend(ContributionDto contribution);

    /**
     * 用户量趋势查询
     *
     * @param list
     * @return
     */
    public RestApiResult querySiteuid(List<String> list);

}

package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.FrstorderDto;
import com.niuren.dsapi.model.dto.NumtrendRgstDto;
import com.niuren.dsapi.service.impl.RestApiResult;

public interface MemSecQueryApi {


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
     * 3.活动数据转化追踪数据查询
     *
     * @param numtrendDto
     * @return
     */
    public RestApiResult queryNumtrendFrstorder(NumtrendRgstDto numtrendDto);

}

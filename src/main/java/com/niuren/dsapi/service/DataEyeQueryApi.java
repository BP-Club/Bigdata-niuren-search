package com.niuren.dsapi.service;

import com.niuren.dsapi.model.dto.DataEyeRequestDto;
import com.niuren.dsapi.service.impl.DsapiResult;

public interface DataEyeQueryApi {


    /**
     * 1.直播间实时数据查询
     *
     * @param saleDto
     * @return
     */
    public DsapiResult querySaleData(DataEyeRequestDto saleDto);


}

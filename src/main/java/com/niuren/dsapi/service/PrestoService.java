package com.niuren.dsapi.service;

import java.util.List;
import java.util.Map;

public interface PrestoService {


    /**
     * 根据参数查询数据
     *
     * @param sql
     * @param sourceType
     * @return
     */
    Object query(String sql, String sourceType);

    Object query(Long id);

    public Object queryPoolTest(String sql, String sourceType);
}

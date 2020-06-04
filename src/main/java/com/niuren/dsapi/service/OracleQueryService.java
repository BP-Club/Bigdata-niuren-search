package com.niuren.dsapi.service;

import java.util.List;
import java.util.Map;

/**
 * oracle 查询连接
 */
public interface OracleQueryService {

    List<Map<String, Object>> query(String sql);
}

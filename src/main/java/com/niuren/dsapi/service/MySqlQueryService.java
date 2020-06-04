package com.niuren.dsapi.service;

import java.util.List;
import java.util.Map;

/**
 * mysql 查询连接
 */
public interface MySqlQueryService {

    List<Map<String, Object>> query(String sql, String sourceCode);
}

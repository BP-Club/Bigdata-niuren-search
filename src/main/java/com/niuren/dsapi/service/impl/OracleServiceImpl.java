package com.niuren.dsapi.service.impl;

import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceOracleConfig;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.service.OracleQueryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: DoubleLuck.Li
 * @description:
 * @create: 2018-09-06 09:51
 **/
@Service
public class OracleServiceImpl implements OracleQueryService {
    @Value("${oracle.datasource.username}")
    private String userName;

    @Value("${oracle.datasource.password}")
    private String password;

    @Value("${oracle.datasource.url}")
    private String jdbcUrl;

    @Value("${oracle.datasource.driver-class-name}")
    private String driverClass;
    private static final Logger log = LoggerFactory.getLogger(OracleServiceImpl.class);
    @Autowired
    private DataSourceOracleConfig dataSourceOracleConfig;


    public List<Map<String, Object>> query(String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //根据类型获取数据源
            // connection = dataSourceOracleConfig.getConn();
            connection = DriverManager.getConnection(jdbcUrl, userName, password);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            // System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");

            int totalSize = 0;
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Map<String, Object>> resultList = new ArrayList<>();
            if (resultSetMetaData != null && resultSet != null) {
                while (resultSet.next()) {
                    Map<String, Object> result = new HashMap<>(resultSetMetaData.getColumnCount());
                    for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                        result.put(resultSetMetaData.getColumnLabel(i + 1), resultSet.getObject(i + 1));
                    }
                    //最大数据限制
                    totalSize++;
                    if (totalSize >= Constant.MAX_SIZE) {
                        throw new ServiceException("out range max size =" + Constant.MAX_SIZE);
                    }
                    resultList.add(result);
                }
            }
            return resultList;
        } catch (Exception e) {
            log.error("Oracle connnect fail", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e1) {
                log.error("can't close Oracle connect ", e1);
                throw new RuntimeException(e1);
            }
        }
    }

}

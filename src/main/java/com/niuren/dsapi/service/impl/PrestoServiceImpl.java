package com.niuren.dsapi.service.impl;

import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceHiveConfig;
import com.niuren.dsapi.config.SysDataSource;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.service.PrestoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: DoubleLuck.Li
 * @description:
 * @create: 2018-09-06 09:51
 **/
@Service

public class PrestoServiceImpl implements PrestoService {


    private static final Logger log = LoggerFactory.getLogger(PrestoServiceImpl.class);

    @Value("${oracle.datasource.username}")
    private String userName;

    @Value("${oracle.datasource.password}")
    private String password;

    @Value("${oracle.datasource.url}")
    private String jdbcUrl;

    @Value("${oracle.datasource.driver-class-name}")
    private String driverClass;

    @Autowired
    private SysDataSource sysDataSource;

    @Autowired
    DataSourceHiveConfig sourceHiveConfig;

    @Override
    public List<Map<String, Object>> query(String sql, String sourceType) {
        long beginTime = System.currentTimeMillis();
        log.info("hive-query begin-time=" + beginTime);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //从连接池获取连接
            connection = sysDataSource.getHiveSource(sourceType).getConnection();
            statement = connection.createStatement();
             // statement.setFetchDirection(ResultSet.FETCH_REVERSE);
            long queryTime = System.currentTimeMillis();
            log.info("query-exe-begin=" + (queryTime));
            resultSet = statement.executeQuery(sql);
            resultSet.setFetchSize(5000);//设置批量读取
            int totalSize = 0;
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            log.info("query-exe-end=" + (System.currentTimeMillis() - queryTime));
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> result = new LinkedHashMap<>(resultSetMetaData.getColumnCount());
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
            log.info("query-exe-data-end=" + (System.currentTimeMillis() - queryTime));
            return resultList;
        } catch (Exception e) {
            log.error("hive connnect fail", e);
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
                log.error("can't close hive connect ", e1);
                throw new ServiceException("can't close hive connect ");
            }
            log.info("hive-query begin-end time=" + (System.currentTimeMillis() - beginTime));
        }
    }

    @Override
    public List<Map<String, Object>> queryPoolTest(String sql, String sourceType) {
        long beginTime = System.currentTimeMillis();
        log.info("hive-query begin-time=" + beginTime);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //从连接池获取连接
            connection =sourceHiveConfig.getDataSourcePool().getPooledConnection().getConnection();
            statement = connection.createStatement();
            statement.setFetchSize(5000);//设置批量读取
            // statement.setFetchDirection(ResultSet.FETCH_REVERSE);
            long queryTime = System.currentTimeMillis();
            log.info("query-exe-begin=" + (queryTime));
            resultSet = statement.executeQuery(sql);
            resultSet.setFetchSize(5000);
            int totalSize = 0;
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            log.info("query-exe-end=" + (System.currentTimeMillis() - queryTime));
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> result = new LinkedHashMap<>(resultSetMetaData.getColumnCount());
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
            log.info("query-exe-data-end=" + (System.currentTimeMillis() - queryTime));
            return resultList;
        } catch (Exception e) {
            log.error("hive connnect fail", e);
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
                log.error("can't close hive connect ", e1);
                throw new ServiceException("can't close hive connect ");
            }
            log.info("hive-query begin-end time=" + (System.currentTimeMillis() - beginTime));
        }
    }


    @Override
    public List<Map<String, Object>> query(Long id) {
        String sql = null;
        // TODO query sql
        return this.query(sql, "hive01");
    }
}

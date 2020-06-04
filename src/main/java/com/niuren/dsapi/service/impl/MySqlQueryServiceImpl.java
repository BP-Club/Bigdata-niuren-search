package com.niuren.dsapi.service.impl;

import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.model.Entity.TempleDbEntity;
import com.niuren.dsapi.model.dao.TempleDbEntityMapper;
import com.niuren.dsapi.service.MySqlQueryService;
import com.niuren.dsapi.util.DataSourceConUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql
 */
@Service
public class MySqlQueryServiceImpl implements MySqlQueryService {

    /**
     * log 日志
     */
    private static final Logger log = LoggerFactory.getLogger(MySqlQueryServiceImpl.class);

    @Autowired
    TempleDbEntityMapper templeDbEntityMapper;

    public List<Map<String, Object>> query(String sql, String sourceCode) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //根据类型获取数据源
            TempleDbEntity dbEntity = new TempleDbEntity();
            dbEntity.setName(sourceCode);//根据名称查询
            dbEntity.setType(Constant.SourceType.MYSQL.getCode().toString());
            //1：查询使用哪个数据库
            List<TempleDbEntity> templeDbList = templeDbEntityMapper.selectListByType(dbEntity);

            TempleDbEntity templeDbEntity = new TempleDbEntity();
            if (!CollectionUtils.isEmpty(templeDbList)) {
                templeDbEntity = templeDbList.get(0);
            }
            connection = new DataSourceConUtils().getConnection(templeDbEntity.getUrl(), templeDbEntity.getUsername(), templeDbEntity.getPassword(),templeDbEntity.getDriver());

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            resultSet.setFetchSize(5000);
            int totalSize = 0;
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Map<String, Object>> resultList = new ArrayList<>();
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
            return resultList;
        } catch (Exception e) {
            log.error("mySql connnect fail", e);
            throw new ServiceException(e.getMessage() + ":" + e.getCause().getMessage());
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
                log.error("can't close MySql connect ", e1);
            }
        }
    }

}

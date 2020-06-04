package com.niuren.dsapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;


@Component
@Order(1)
public class  DataSourceOracleConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceOracleConfig.class);
    /**
     * Hive数据源
     */
    private Connection conn;

    @Value("${oracle.datasource.username}")
    private String userName;

    @Value("${oracle.datasource.password}")
    private String password;

    @Value("${oracle.datasource.url}")
    private String jdbcUrl;

    @Value("${oracle.datasource.driver-class-name}")
    private String driverClass;

    public Connection getConn() {
        try {
            log.info("******DataSourceOracleConfig-getConn begin******");
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(jdbcUrl, userName, password);
            }
            log.info("******DataSourceOracleConfig-getConn end******");
        } catch (Exception e) {
            log.error("DataSourceOracleConfig-getConn-fail", e);
        }

        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("******DataSourceOracleConfig-loading begin******");
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            log.info("******DataSourceOracleConfig-loading end******");
        } catch (Exception e) {
            log.error("DataSourceOracleConfig-connection-fail", e);
        }
    }
}

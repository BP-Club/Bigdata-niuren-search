package com.niuren.dsapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;


public class DataSourceConUtils {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConUtils.class);
    private Connection conn;

    public Connection getConnection(String jdbcUrl, String userName, String password, String diver) {
        try {
            log.info("******DataSourceConUtils-getConn begin******");
            Class.forName(diver);
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            log.info("******DataSourceConUtils-getConn end******");
        } catch (Exception e) {
            log.error("DataSourceConUtils-getConn-fail", e);
        }
        return conn;
    }

}

package com.niuren.dsapi.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-05-29 09:45
 **/
@Component
@Order(1)
public class InfluxDBConfig {
    private static final Logger log = LoggerFactory.getLogger(InfluxDBConfig.class);

    private InfluxDB influxDB;

//    @Value("${influxDB.url}")
    private String url;

//    @Value("${influxDB.username}")
    private String username;

//    @Value("${influxDB.password}")
    private String password;

//    @Value("${influxDB.database}")
    private String database;

//    @Value("${influxDB.realTimebase}")
    private String realTimeDatabase;


    @Value("${influxDB.policy}")
    private String policy;


    /**
     * 连接时序数据库；获得InfluxDB
     **/

    @PostConstruct
    public InfluxDB getInfluxDBConnect() {
        log.info("*******************InfluxDB-getInfluxDBConnect init begin*******************");
        try {
            if (influxDB == null) {
                influxDB = InfluxDBFactory.connect(url, username, password);
            }
            if (influxDB != null) {
                log.info(" ******InfluxDBConnect connect success ******");
            } else {
                log.info(" ******InfluxDB-getInfluxDBConnect connect fail ******");

            }
        } catch (Exception e) {
            log.error("influx DB connect fail:", e);
        }
        log.info("*******************InfluxDB-getInfluxDBConnect init end*******************");
        return influxDB;
    }


    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getRealTimeDatabase() {
        return realTimeDatabase;
    }

    public void setRealTimeDatabase(String realTimeDatabase) {
        this.realTimeDatabase = realTimeDatabase;
    }
}

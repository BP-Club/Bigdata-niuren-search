package com.niuren.dsapi.config;

import com.niuren.dsapi.util.KafkaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;



@Component
@Order(1)
public class DataSourceKafkaConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceKafkaConfig.class);
    @Value("${kafka01.brokenlist}")
    private String brokenList;

    @Value("${kafka01.zkserver}")
    private String zkServer;

    KafkaUtil kafkaUtil = null;

    public KafkaUtil getKafkaUtil() {
        return kafkaUtil;
    }

    public void setKafkaUtil(KafkaUtil kafkaUtil) {
        this.kafkaUtil = kafkaUtil;
    }

    public String getZkServer() {
        return zkServer;
    }

    public void setZkServer(String zkServer) {
        this.zkServer = zkServer;
    }

    public String getBrokenList() {
        return brokenList;
    }

    public void setBrokenList(String brokenList) {
        this.brokenList = brokenList;
    }

    @PostConstruct
    public void init() {
        try {
            kafkaUtil = new KafkaUtil(brokenList, zkServer);
        } catch (Exception e) {
            log.error("***** HbaseConfig-initUrl fail***** =", e);
        }

        log.info("***** HbaseConfig-initUrl end***** =");
    }
}

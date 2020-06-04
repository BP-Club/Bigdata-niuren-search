package com.niuren.dsapi.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Order(1)
public class DataSourceHbaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceHbaseConfig.class);

    @Value("${hbase.zookeeper.quorum}")
    private String quorum;

    @Value("${hbase.zookeeper.port}")
    private String port;


    @PostConstruct
    public void init() {

        log.info("***** HbaseConfig-initUrl end***** =");
    }
}

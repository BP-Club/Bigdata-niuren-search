package com.niuren.dsapi.config;

import com.niuren.dsapi.aspect.ControllerAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;



@Component
@Order(1)
public class DataSourceEsConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceEsConfig.class);

    //aws es url
    private String postUrl;

    @Value("${es.url}")
    private String esUrl;

    @Value("${es.port}")
    private String esPort;

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getEsPort() {
        return esPort;
    }

    public void setEsPort(String esPort) {
        this.esPort = esPort;
    }

    public String getEsUrl() {
        return esUrl;
    }

    public void setEsUrl(String esUrl) {
        this.esUrl = esUrl;
    }

    @PostConstruct
    public void initUrl() {
        this.postUrl = "http://" + esUrl + ":" + esPort;
        log.info("***** EsConfig-initUrl***** =" + postUrl);
    }
}

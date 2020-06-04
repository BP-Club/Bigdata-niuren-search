package com.niuren.dsapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: dailinwei
 * @description: session 配置
 * @create: 2018-05-02 17:59
 **/
@Component
@Configuration
public class HttpSessionConfig {

    @Getter
    @Value("#{'${session.login.path.exclude}'.split(',')}")
    private List<String> urlList;

}

package com.niuren.dsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统心跳检测
 */

@RestController
@RequestMapping("/dsapi")
public class MemberIndex {


    private static final Logger log = LoggerFactory.getLogger(MemberIndex.class);

    /**
     * 心跳检测页
     *
     * @return
     */
    @GetMapping("/heart")
    public String heart() {
        return "OK";
    }
}

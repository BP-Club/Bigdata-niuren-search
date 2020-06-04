package com.niuren.dsapi.model.dto;

import java.util.List;

public class SendKafkaDataDto {
    /**
     *
     */
    String templeCode;
    /**
     * 发送
     */
    List<Object> sendList;

    public String getTempleCode() {
        return templeCode;
    }

    public void setTempleCode(String templeCode) {
        this.templeCode = templeCode;
    }

    public List<Object> getSendList() {
        return sendList;
    }

    public void setSendList(List<Object> sendList) {
        this.sendList = sendList;
    }
}

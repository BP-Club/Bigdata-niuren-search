package com.niuren.dsapi.exception;

import com.niuren.dsapi.service.impl.RestApiResult;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-07-05 17:39
 **/
public class ServiceException extends RuntimeException {

    private int errorCode;

    public ServiceException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(String message) {
        super(message);
        this.errorCode = -1;
    }


}

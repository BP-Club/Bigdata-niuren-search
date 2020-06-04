package com.niuren.dsapi.exception;

import com.niuren.dsapi.service.impl.RestApiResult;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-07-17 09:58
 **/
public class RepositoryException extends RuntimeException {

    private int errorCode;

    public RepositoryException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RepositoryException(String message) {
        super(message);
        this.errorCode = -1;
    }

}

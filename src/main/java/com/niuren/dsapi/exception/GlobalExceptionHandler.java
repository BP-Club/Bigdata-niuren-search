package com.niuren.dsapi.exception;

import com.niuren.dsapi.controller.OracleDataController;
import com.niuren.dsapi.service.impl.RestApiResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.UndeclaredThrowableException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = RepositoryException.class)
    @ResponseBody
    public RestApiResult repositoryErrorHandler(RepositoryException e) {
        log.error("", e);
        return new RestApiResult(e);
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public RestApiResult serviceErrorHandler(ServiceException e) {
        log.error("", e);
        return new RestApiResult(e);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestApiResult defaultErrorHandler(Exception e) {
        if (e instanceof UndeclaredThrowableException) {
            e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        log.error("", e);
        return new RestApiResult(e);
    }

}

package com.niuren.dsapi.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-07-17 15:01
 **/
@Component
@Aspect
public class ControllerAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerAspect.class);

    @Pointcut("execution(* com.niuren.dsapi.controller..*(..))")
    public void controllerPoint() {

    }

    @Before("controllerPoint()")
    public void logParam(JoinPoint point) {
        log.info("{}.{}, params : {}", point.getTarget(), point.getSignature().getName(), point.getArgs());
    }

    @AfterReturning(returning = "result", pointcut = "controllerPoint()")
    public void logApiResult(JoinPoint point, Object result) {
        log.info("{}.{}, result : {}", point.getTarget(), point.getSignature().getName(), result);
    }

}

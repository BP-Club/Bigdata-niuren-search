package com.niuren.dsapi.aspect;

import com.niuren.dsapi.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-07-12 18:40
 **/
@Component
@Aspect
public class ServiceAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceAspect.class);

    @Pointcut("execution(* com.niuren.dsapi.service..*(..))")
    public void servicePoint() {
    }

    @Around("servicePoint()")
    public Object handleExceptionAndTime(ProceedingJoinPoint point) {
        try {
            long start = System.currentTimeMillis();
            Object result = point.proceed();
            long end = System.currentTimeMillis();
            long duration = end - start;
            if (duration > 50) {
                log.warn("{}.{} consume {}ms", point.getTarget(), point.getSignature().getName(), duration);
            } else if (duration > 10) {
                log.info("{}.{} consume {}ms", point.getTarget(), point.getSignature().getName(), duration);
            } else {
                log.debug("{}.{} consume {}ms", point.getTarget(), point.getSignature().getName(), duration);
            }
            return result;
        } catch (Throwable throwable) {
            log.error("error at {}.{}, cause : {}", point.getTarget(), point.getSignature().getName(), throwable.getMessage());
            throw new ServiceException(throwable.getMessage());
        }
    }

}

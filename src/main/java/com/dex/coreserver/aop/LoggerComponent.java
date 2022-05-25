package com.dex.coreserver.aop;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Log4j2
public class LoggerComponent {

    @Pointcut("execution(* com.dex.coreserver.service.*.*(..))")
    private void serviceMethods() {}

    @Pointcut("within(com.dex.coreserver.controller..*)")
    private void controllerMethods() {}

    @Around("serviceMethods()")
    @SneakyThrows
    public Object logArroundService(ProceedingJoinPoint pjp) {
        log.info("{}.{}() with inputs={}",pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),Arrays.toString(pjp.getArgs()));
        Object proceed = pjp.proceed();
        log.info("{}.{} with outputs={}",pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),proceed);
        return proceed;
    }

    @Around("controllerMethods()")
    @SneakyThrows
    public Object logArroundController(ProceedingJoinPoint pjp) {
        log.info("Request for {}.{}() with arguments{}" ,pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),Arrays.toString(pjp.getArgs()));
        Object proceed = pjp.proceed();
        log.info("Response for {}.{}() with result{}",pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),proceed);
        return proceed;
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "e")
    public void logAfterException(JoinPoint jp, Exception e) {
        log.error("Exception in {}.{} with cause = {}, message = {}",
                jp.getSignature().getDeclaringTypeName(),jp.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage() != null ? e.getMessage() : "NULL");
    }

}

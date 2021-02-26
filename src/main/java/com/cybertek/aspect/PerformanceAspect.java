package com.cybertek.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class PerformanceAspect {


    @Pointcut("@annotation(com.cybertek.annotation.ExecutionTime)")
    public void anyExecutionTimeOperation() {
    }

    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        long beforeTime = System.currentTimeMillis();
        Object result = null;

        result = proceedingJoinPoint.proceed();


        long afterTime = System.currentTimeMillis();

        log.info("Time taken to execute: {} ms Methog:{} - Parameters:{}", (afterTime - beforeTime), proceedingJoinPoint.getSignature().toShortString(), proceedingJoinPoint.getArgs());

        return result;
    }
}

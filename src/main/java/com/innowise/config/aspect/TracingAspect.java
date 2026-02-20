package com.innowise.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method execution time.
 * Logs execution time for controllers and services.
 */
@Aspect
@Component
@Slf4j
public class TracingAspect {

    /**
     * Logs execution time for REST controller methods.
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        log.debug("Starting execution of {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Completed execution of {}.{} in {}ms", className, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error in {}.{} after {}ms: {}", className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Logs execution time for service methods.
     */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        log.debug("Starting execution of {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Completed execution of {}.{} in {}ms", className, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error in {}.{} after {}ms: {}", className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }
}

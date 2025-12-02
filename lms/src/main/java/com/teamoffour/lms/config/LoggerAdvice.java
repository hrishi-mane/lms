package com.teamoffour.lms.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logging Aspect for method entry/exit logging
 * Uses Spring AOP to log all service layer methods
 */
@Aspect
@Component
public class LoggerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAdvice.class);

    /**
     * Pointcut: All methods in service layer
     */
    @Pointcut("execution(* com.teamoffour.lms.service..*(..))")
    public void serviceMethods() {}

    /**
     * Logs method entry, exit, and execution time
     */
    @Around("serviceMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // Log entry
        logger.info("→ ENTERING: {} with args: {}", methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            // Execute method
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // Log exit (success)
            logger.info("← EXITING: {} | Time: {} ms", methodName, executionTime);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            // Log exit (exception)
            logger.error("✗ EXCEPTION in: {} | Time: {} ms | Error: {}",
                    methodName, executionTime, e.getMessage());

            throw e;
        }
    }
}
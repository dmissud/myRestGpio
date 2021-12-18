package org.dbs.pi4j.myrestgpio.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.Timer;

@Slf4j
@Aspect
@Configuration
public class LogAspect {

    private String packageApiName ="org.dbs.pi4j.myrestgpio.myrest";

    @Pointcut("@annotation(org.dbs.pi4j.myrestgpio.common.LogException)")
    public void logException() {
    }

    @AfterThrowing(pointcut ="logException()", throwing = "ex")
    public void logAfterThrowingAllMethods(Exception ex) {
        log.error(ex.getMessage(), ex);
    }

    @Around("@annotation(org.dbs.pi4j.myrestgpio.common.LogExecutionTime)")
    public Object metricGlobal(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ProceedingJoinPoint pjp;
        MethodSignature method = (MethodSignature) proceedingJoinPoint.getSignature();
        LogExecutionTime ann = method.getMethod().getAnnotation(LogExecutionTime.class);
        Timer timer = new Timer();
        Timer.Context context = timer.time();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            double timeInSecond = context.stop() / 1000000000.0;
            MDC.put("endPoints", ann.value());
            MDC.put("metrics", String.valueOf(timeInSecond));
            log.info("Temps écoulé pour l'API {} ({}) : {} sec", ann.valueType(), proceedingJoinPoint.getSignature().getName(), timeInSecond);
            MDC.remove("metrics");
            MDC.remove("endPoints");
        }
    }
}

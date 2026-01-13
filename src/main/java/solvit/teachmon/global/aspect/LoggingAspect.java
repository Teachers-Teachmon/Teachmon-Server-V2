package solvit.teachmon.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("@annotation(solvit.teachmon.global.annotation.Trace) || @within(solvit.teachmon.global.annotation.Trace)")
    public void loggingTraceAnnotationPointcut() {}

    @Before("loggingTraceAnnotationPointcut()")
    public void logBefore(final JoinPoint joinPoint) {
        log.info("[TRACE][{}][START] arguments={}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "loggingTraceAnnotationPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("[TRACE][{}][END] result={}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "loggingTraceAnnotationPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.warn("[TRACE][{}][ERROR] exClass={}, exMessage={}",
                joinPoint.getSignature().getName(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                e);
    }
}

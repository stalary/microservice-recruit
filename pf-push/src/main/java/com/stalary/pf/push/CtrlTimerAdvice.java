
package com.stalary.pf.push;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

/**
 * CtrlTimerAdvice
 *
 * @author lirongqian
 * @since 2018/04/09
 */

@Slf4j
@Aspect
@Component
public class CtrlTimerAdvice {

    @Around("@annotation(requestMapping)")
    public Object requestMappingAdvice(ProceedingJoinPoint thisJoinPoint, RequestMapping requestMapping) throws Throwable {
        String path = Arrays.toString(requestMapping.value());
        return process(thisJoinPoint, path);
    }

    @Around("@annotation(getMapping)")
    public Object getMappingAdvice(ProceedingJoinPoint thisJoinPoint, GetMapping getMapping) throws Throwable {
        String path = Arrays.toString(getMapping.value());
        return process(thisJoinPoint, path);
    }

    @Around("@annotation(postMapping)")
    public Object getMappingAdvice(ProceedingJoinPoint thisJoinPoint, PostMapping postMapping) throws Throwable {
        String path = Arrays.toString(postMapping.value());
        return process(thisJoinPoint, path);
    }

    private Object process(ProceedingJoinPoint pjp, String path) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        long endTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String logName = methodSignature.getMethod().getName() + "(" + path + ")";
        log.info(logName + ".time=" + (endTime - startTime));
        log.info(logName + " Args: " + Arrays.toString(pjp.getArgs()));
        return result;
    }
}

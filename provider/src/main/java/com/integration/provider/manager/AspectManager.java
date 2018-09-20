package com.integration.provider.manager;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by Vin on 2018/6/13.
 */
@Component
@Aspect
@Slf4j
public class AspectManager {

    @Pointcut("execution(public * com.integration.provider.controller.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void deBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.debug("方法执行前...");
        log.debug("URL : " + request.getRequestURL().toString());
        log.debug("HTTP_METHOD : " + request.getMethod());
        log.debug("IP : " + request.getRemoteAddr());
        log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.debug("ARGS : " + Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        log.debug("方法的返回值 : " + ret);
    }

    //后置异常通知
    @AfterThrowing("pointCut()")
    public void throwing(JoinPoint joinPoint){
        log.debug("方法异常时执行.....");
        log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("pointCut()")
    public void after(JoinPoint joinPoint){
        log.debug("方法最后执行.....");
        log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) {
        log.debug("方法环绕start.....");
        try {
            Object o =  pjp.proceed();
            log.debug("方法环绕proceed，结果是 :" + o);
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}

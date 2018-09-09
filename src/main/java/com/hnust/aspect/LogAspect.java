package com.hnust.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by BigBoss on 2017/5/28.
 */

/**
 * 通过注解@Aspect 表示这是面向切面的
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    //在执行IndexController类里头方法之前，都要先执行beforeMethod()方法
    @Before("execution(* com.hnust.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            sb.append("arg:" + arg.toString() + "|");
        }
        logger.info("before:time", new Date());
        logger.info("before method:" + sb.toString());
    }
    //在执行IndexController类里头方法之后，都要执行afterMethod()方法
    @After("execution(* com.hnust.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint ) {
        logger.info("after method:");
    }
}

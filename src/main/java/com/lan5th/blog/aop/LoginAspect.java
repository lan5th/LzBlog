package com.lan5th.blog.aop;

import com.lan5th.blog.controller.HtmlController;
import com.lan5th.blog.utils.UserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author lan5th
 * @date 2022/6/28 13:35
 */
@Component
@Aspect
public class LoginAspect {
    @Pointcut("execution(* com.lan5th.blog.controller.*.*(..))")
    public void loginCut(){}
    
    @Before("loginCut()")
    public void deBefore(JoinPoint joinPoint) {
        if (joinPoint.getSignature().getDeclaringType() == HtmlController.class)
            return;
        boolean res = UserUtil.verifyToken();
    }
    
    @After("loginCut()")
    public void after(JoinPoint joinPoint){
        if (joinPoint.getSignature().getDeclaringType() == HtmlController.class)
            return;
        UserUtil.cleanIdCache();
    }
}

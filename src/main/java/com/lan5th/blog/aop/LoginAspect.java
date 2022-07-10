package com.lan5th.blog.aop;

import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.controller.HtmlController;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.utils.JsonObject;
import com.lan5th.blog.utils.UserUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * User信息验证切面类
 * @author lan5th
 * @date 2022/6/28 13:35
 */
@Component
@Aspect
public class LoginAspect {
    @Pointcut("execution(* com.lan5th.blog.controller.*.*(..))")
    public void loginCut(){}
    
    /**
     * 用户信息相关验证
     * 注意Around需要手动放行
     * @param joinPoint
     * @return 这里返回的JsonObject会替代成为Controller执行的返回结果
     * @throws Throwable
     */
    @Around(value = "loginCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        Object result;
        if (signature.getDeclaringType() != HtmlController.class && signature instanceof MethodSignature) {
            //验证token
            UserUtil.verifyToken();
            User user = UserUtil.getCurrentUser();
    
            MethodSignature methodSignature = (MethodSignature) signature;
            RequireToken requireToken = methodSignature.getMethod().getAnnotation(RequireToken.class);
            if (requireToken != null) {
                if (user == null) {
                    JsonObject res = new JsonObject();
                    res.setStatus(false);
                    res.setMessage("您未登录，无法进行相关操作");
                    return res;
                }
                if (requireToken.requireAdmin()) {
                    if (!user.isAdmin()) {
                        JsonObject res = new JsonObject();
                        res.setStatus(false);
                        res.setMessage("您的权限不足，无法进行操作");
                        return res;
                    }
                }
            }
    
            //放行
            result = joinPoint.proceed();
    
            if (result instanceof JsonObject) {
                //封装User
                if (user != null) {
                    ((JsonObject) result).put("user", user);
                }
            }
            UserUtil.cleanIdCache();
        } else {
            result = joinPoint.proceed();
        }
    
        return result;
    }
}

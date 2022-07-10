package com.lan5th.blog.interceptor;

import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.controller.HtmlController;
import com.lan5th.blog.utils.UserUtil;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 这里仅验证是否登录
 * @author lan5th
 * @date 2022/7/4 10:08
 */
@Deprecated
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //只有添加了@RequireToken注解的方法才需要拦截验证
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //HtmlController不论什么情况都直接放行
        if (handlerMethod.getBeanType() == HtmlController.class) {
            return true;
        }
    
        //不论是否添加@RequireToken注解都需要先验证token
        boolean verifyResult = UserUtil.verifyToken();
        
        Method method = handlerMethod.getMethod();
        if (!method.isAnnotationPresent(RequireToken.class)) {
            return true;
        }
        return verifyResult;
    }
    
    //结束时清除id缓存，防止内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserUtil.cleanIdCache();
    }
}

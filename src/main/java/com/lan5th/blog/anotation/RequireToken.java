package com.lan5th.blog.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前前端跳转页面时不能携带token，因此只能在xhr方法上使用该注解
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireToken {
    boolean required() default true;
    boolean requireAdmin() default true;
}

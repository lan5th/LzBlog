package com.lan5th.blog.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequireToken {
    boolean required() default true;
}

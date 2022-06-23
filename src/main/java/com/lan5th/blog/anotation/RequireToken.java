package com.lan5th.blog.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequireToken {
    boolean required() default true;
}
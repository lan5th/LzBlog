package com.lan5th.blog.utils.context;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public final class LoginContext {
    private static final ThreadLocal userContext = new ThreadLocal();
    
    public static void createContext() {
    
    }
}

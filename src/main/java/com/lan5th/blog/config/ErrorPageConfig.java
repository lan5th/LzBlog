package com.lan5th.blog.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage p1 = new ErrorPage(HttpStatus.BAD_REQUEST, "/html/404");
        ErrorPage p2 = new ErrorPage(HttpStatus.UNAUTHORIZED, "/html/404");
        ErrorPage p3 = new ErrorPage(HttpStatus.PAYMENT_REQUIRED, "/html/404");
        ErrorPage p4 = new ErrorPage(HttpStatus.FORBIDDEN, "/html/404");
        ErrorPage p5 = new ErrorPage(HttpStatus.NOT_FOUND, "/html/404");
        registry.addErrorPages(p1, p2, p3, p4, p5);
    }
}

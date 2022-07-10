package com.lan5th.blog.config;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Configuration
public class MvcConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return   new WebMvcConfigurer() {
            /**
             * 首页设置
             * @param registry
             */
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("html/index");
                registry.addViewController("/index.html").setViewName("html/index");
            }

//            /**
//             * 注册拦截器
//             * @param registry
//             */
//            @Override
//            public void addInterceptors(InterceptorRegistry registry) {
//                registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
//            }
    
//            /**
//             * 静态资源虚拟地址映射
//             * 文件上传读取相关
//             * @param registry
//             */
//            @Override
//            public void addResourceHandlers(ResourceHandlerRegistry registry) {
//                //获取jar包物理路径
//                ApplicationHome ah = new ApplicationHome(getClass());
//                File jarFile = ah.getSource();
//                String filePath = jarFile.getParentFile().getPath() + "/upload";
//                System.out.println("初始化文件上传路径:" + filePath);
//                registry.addResourceHandler("/app_file/**")
//                        .addResourceLocations("file:" + filePath + "/") ;
//            }
        };
    }
}

package com.niubaide.im.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {

    // 这么写的目的是为了在SessionInterceptor中能注入spring中的service

    private final UserAuthInteceptor userAuthInteceptor;

    public MyWebAppConfigurer(UserAuthInteceptor userAuthInteceptor) {
        this.userAuthInteceptor = userAuthInteceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(userAuthInteceptor).addPathPatterns("/**")
                // 排除路径
                .excludePathPatterns("/login")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/img/*")
                .excludePathPatterns("/toLogin");
        super.addInterceptors(registry);
    }
}

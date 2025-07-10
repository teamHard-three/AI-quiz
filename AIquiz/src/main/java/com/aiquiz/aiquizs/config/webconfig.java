package com.aiquiz.aiquizs.config;

import com.aiquiz.aiquizs.Interceptors.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class webconfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor()).addPathPatterns("/**") //拦截所有请求
                .excludePathPatterns("/user/login", "/user/register"); //排除登录和注册请求
    }
}
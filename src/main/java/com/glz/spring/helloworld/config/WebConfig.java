package com.glz.spring.helloworld.config;

import com.glz.spring.helloworld.interceptor.AuthInterceptor;
import com.glz.spring.helloworld.interceptor.SimpleLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SimpleLogInterceptor simpleLogInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){

//        registry.addInterceptor(simpleLogInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/ai/login","/ai/error")
//                .order(1);
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/**")
//                .order(2);
    }

}

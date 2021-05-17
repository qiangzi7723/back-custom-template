package com.custom.config;

import com.custom.config.interceptor.CORSInterceptor;
import com.custom.config.interceptor.ContextInterceptor;
import com.custom.config.auth.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    @Autowired
    ContextInterceptor contextInterceptor;

    @Autowired
    JwtInterceptor jwtInterceptor;

    @Autowired
    CORSInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor);
        registry.addInterceptor(contextInterceptor);
        registry.addInterceptor(jwtInterceptor);
    }

}

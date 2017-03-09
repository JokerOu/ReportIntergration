package com.pccw.hkt.cascade;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.pccw.hkt.cascade.interceptor.SessionInterceptor;

@Configuration
public class InterceptorAdapter extends WebMvcConfigurerAdapter{
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor()).excludePathPatterns("/", "/login");
        super.addInterceptors(registry);
    }

}

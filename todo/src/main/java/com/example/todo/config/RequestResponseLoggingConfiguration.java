package com.example.todo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestResponseLoggingConfiguration {
   @Autowired
    private RequestResponseLoggingFilter requestResponseLoggingFilter;

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestResponseLoggingFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestResponseLoggingFilter");
        registration.setOrder(1);
        return registration;
    }
}

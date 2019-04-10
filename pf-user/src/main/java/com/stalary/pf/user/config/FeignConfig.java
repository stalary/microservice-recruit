package com.stalary.pf.user.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * FeignConfig
 *
 * @author lirongqian
 * @since 2018/10/17
 */
@Configuration
public class FeignConfig {

    @Bean
    public Request.Options options() {
        // 超时10s
        return new Request.Options(20000, 10000);
    }

    @Bean
    public Retryer feignRetryer() {
        // 重试3次
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 打印全日志
        return Logger.Level.FULL;
    }


    @Bean
    public RequestInterceptor headerInterceptor() {
        // 传递header
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        requestTemplate.header(name, values);
                    }
                }
            }
        };
    }
}
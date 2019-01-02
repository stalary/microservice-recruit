/**
 * @(#)Config.java, 2019-01-02.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Config
 *
 * @author lirongqian
 * @since 2019/01/02
 */
@Configuration
@ConfigurationProperties(prefix = "pf")
@Data
@Component
public class CoreConfig {

    /** 需要验证登陆的接口 **/
    private List<String> authList;
}
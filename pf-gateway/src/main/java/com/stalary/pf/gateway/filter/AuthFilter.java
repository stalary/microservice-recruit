/**
 * @(#)AuthFilter.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.gateway.filter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.stalary.pf.gateway.client.UserCenterClient;
import com.stalary.pf.gateway.data.ProjectInfo;
import com.stalary.pf.gateway.data.ResponseMessage;
import com.stalary.pf.gateway.data.User;
import com.stalary.pf.gateway.exception.MyException;
import com.stalary.pf.gateway.exception.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * AuthFilter
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Resource
    private UserCenterClient userCenterClient;

    private String projectKey;

    /** 需要验证登陆的接口 **/
    @Value("${pf.authArray}")
    private String[] authArray;

    /** 存储用户id缓存 **/
    private LoadingCache<String, String> userIdCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .initialCapacity(10)
            .maximumSize(100)
            .recordStats()
            .build(key -> {
                ResponseMessage<User> userInfo = userCenterClient.getUserInfo(key, projectKey);
                if (userInfo.isSuccess()) {
                    return String.valueOf(userInfo.getData().getId());
                } else {
                    return "";
                }
            });

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("Authorization")).orElse("");
        if (StringUtils.isNotEmpty(token)) {
            token = token.split(" ")[1];
        }
        if (StringUtils.isEmpty(projectKey)) {
            ResponseMessage<ProjectInfo> project = userCenterClient.getProjectInfo("leader直聘", "17853149599");
            if (project.isSuccess()) {
                projectKey = project.getData().getKey();
            }
        }
        String userId = "";
        if (StringUtils.isNotEmpty(token)) {
            userId = Optional.ofNullable(userIdCache.get(token)).orElse("");
        }
        // 对需要登陆的接口进行验证
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        List<String> authList = Lists.newArrayList(authArray);
        if (authList.contains(path) && StringUtils.isBlank(userId)) {
            throw new MyException(ResultEnum.NEED_LOGIN);
        }
        ServerHttpRequest host = exchange.getRequest().mutate().header("userId", userId).build();
        ServerWebExchange build = exchange.mutate().request(host).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
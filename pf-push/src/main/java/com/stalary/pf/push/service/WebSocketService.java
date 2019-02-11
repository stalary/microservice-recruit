/**
 * @(#)WebSocketService.java, 2018-06-07.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.push.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.TimeUnit;

/**
 * WebSocketService
 *
 * @author lirongqian
 * @since 2018/06/07
 */
@Service
@ServerEndpoint("/push/ws/{userId}")
@Slf4j
@Data
public class WebSocketService {

    private static Cache<Long, WebSocketService> sessionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .concurrencyLevel(8)
            .maximumSize(1000)
            .recordStats()
            .build();

    private Long userId;

    private Session session;

    private String message;

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("userId") Long userId) {
        log.info("userId: " + userId + " webSocket开始连接");
        this.userId = userId;
        this.session = session;
        WebSocketService present = sessionCache.getIfPresent(userId);
        if (present != null && StringUtils.isNotEmpty(present.getMessage())) {
            present.setSession(this.session);
            this.message = present.getMessage();
            sendMessage(userId, this.message);
        } else {
            sessionCache.put(userId, this);
        }
        log.info("sessionCache: " + sessionCache.asMap().keySet());
    }


    @OnClose
    public void onClose() {
        log.info("userId: " + this.userId + " webSocket关闭连接");
        WebSocketService present = sessionCache.getIfPresent(this.userId);
        if (present != null) {
            // 退出时清空session
            present.setSession(null);
            sessionCache.put(this.userId, present);
        }
    }

    @OnError
    public void onError(Throwable e) {
        log.warn("webSocket error! ", e);
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【webSocket】收到客户端发来的消息:{}", message);
    }

    /**
     * 向客户端发送消息
     **/
    @SneakyThrows
    public void sendMessage(Long userId, String message) {
        WebSocketService socket = sessionCache.getIfPresent(userId);
        // socket连接时直接发送消息
        if (socket != null && socket.getSession() != null) {
            socket.session
                    .getBasicRemote()
                    .sendText(message);
            // 更新消息的信息，进行缓存
            socket.setMessage(message);
            sessionCache.put(userId, socket);
            log.info("【webSocket】 send message: userId: " + userId + " : message:" + message);
        } else {
            // 未连接时暂存消息
            WebSocketService webSocketService = new WebSocketService();
            webSocketService.setUserId(userId);
            webSocketService.setMessage(message);
            sessionCache.put(userId, webSocketService);
            log.info("save: userId: " + userId + " : message:" + message);
        }
    }

}
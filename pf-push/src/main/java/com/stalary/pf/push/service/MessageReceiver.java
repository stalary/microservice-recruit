
package com.stalary.pf.push.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.pf.push.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * MessageReceiver
 *
 * @author lirongqian
 * @since 2019/02/25
 */
@Service
@Slf4j
public class MessageReceiver {

    public static String WS_CHANNEL = "ws";

    @Resource
    private WebSocketService webSocketService;

    public void receiveMessage(String message, String channel) {
        log.info("receive message" + channel + message);
        if (WS_CHANNEL.equals(channel)) {
            if (StringUtils.isNotEmpty(message)) {
                WsMessage wsMessage = JSONObject.parseObject(message, WsMessage.class);
                webSocketService.sendMessage(wsMessage.getUserId(), wsMessage.getMessage());
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WsMessage {
        private Long userId;

        private String message;
    }

}
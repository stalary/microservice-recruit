/**
 * @(#)MessageClient.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.Message;
import com.stalary.pf.consumer.data.dto.ResponseMessage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * MessageClient
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@FeignClient(name = "pf-message")
@Component
public interface MessageClient {

    /** 存储站内信 **/
    @PostMapping("/message/save")
    void saveMessage(@RequestBody Message message);

    /** 获取未读站内信数量 **/
    @GetMapping("/message/count/not")
    ResponseMessage<Integer> getNotReadCount(@RequestParam("userId") Long userId);
}
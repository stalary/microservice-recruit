/**
 * @(#)MessageClient.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.Message;
import com.stalary.pf.consumer.data.dto.ResponseMessage;
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
@FeignClient(name = "message", url = "${message.server}")
@Component
public interface MessageClient {

    @PostMapping("/message/save")
    void saveMessage(@RequestBody Message message);

    @GetMapping("/message/count/not")
    ResponseMessage<Integer> getNotReadCount(@RequestParam("userId") Long userId);
}
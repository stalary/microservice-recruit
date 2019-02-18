/**
 * @(#)PushClient.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * PushClient
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@FeignClient(name = "pf-push")
@Component
public interface PushClient {

    @GetMapping("/push/send")
    void sendMessage(@RequestParam("userId") Long userId, @RequestParam("message") String message);
}
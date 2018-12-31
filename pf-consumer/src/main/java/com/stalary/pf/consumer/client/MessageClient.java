/**
 * @(#)MessageClient.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * MessageClient
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@FeignClient(name = "message", url = "${gateway.server}")
@Component
public interface MessageClient {
}
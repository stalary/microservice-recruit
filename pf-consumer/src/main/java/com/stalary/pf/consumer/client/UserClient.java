package com.stalary.pf.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author Stalary
 * @description
 * @date 2018/12/31
 */
@FeignClient(name = "user", url = "${gateway.server}")
@Component
public interface UserClient {
}

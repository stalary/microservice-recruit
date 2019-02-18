package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.ResponseMessage;
import com.stalary.pf.consumer.data.dto.UserInfo;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Stalary
 * @description
 * @date 2018/12/31
 */
@FeignClient(name = "pf-user")
@Component
public interface UserClient {

    @GetMapping("/user/info")
    ResponseMessage<UserInfo> getUserInfo(@RequestParam("userId") Long userId);

    @GetMapping("/user/email")
    ResponseMessage<String> getEmail(@RequestParam("userId") Long userId);
}

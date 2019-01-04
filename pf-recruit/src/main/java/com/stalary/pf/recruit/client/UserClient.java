package com.stalary.pf.recruit.client;

import com.stalary.pf.recruit.data.dto.User;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
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
@FeignClient(name = "user", url = "${user.server}")
@Component
@RefreshScope
public interface UserClient {

    @GetMapping("/user/userId")
    ResponseMessage<User> getUser(@RequestParam("userId") Long userId);
}

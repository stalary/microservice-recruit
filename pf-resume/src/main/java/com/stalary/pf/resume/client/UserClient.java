package com.stalary.pf.resume.client;

import com.stalary.pf.resume.data.dto.UserInfo;
import com.stalary.pf.resume.data.vo.ResponseMessage;
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
}

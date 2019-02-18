package com.stalary.pf.outside.client;

import com.stalary.pf.outside.data.ResponseMessage;
import com.stalary.pf.outside.data.UploadAvatar;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Stalary
 * @description
 * @date 2019/1/1
 */
@FeignClient(name = "pf-user")
@Component
public interface UserClient {

    @PostMapping("/user/avatar")
    ResponseMessage uploadAvatar(@RequestBody UploadAvatar uploadAvatar);
}

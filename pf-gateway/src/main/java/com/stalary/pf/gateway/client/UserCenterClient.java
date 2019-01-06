package com.stalary.pf.gateway.client;

import com.stalary.pf.gateway.data.ProjectInfo;
import com.stalary.pf.gateway.data.ResponseMessage;
import com.stalary.pf.gateway.data.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户中心调用
 * @author Stalary
 * @description
 * @date 2018/12/28
 */
@FeignClient(name = "usercenter", url = "${server.user}")
@Component
public interface UserCenterClient {

    /**
     * @method getProjectInfo 获取项目信息
     * @param name 项目名称
     * @param phone 手机号
     * @return 项目信息
     **/
    @GetMapping("/facade/project")
    ResponseMessage<ProjectInfo> getProjectInfo(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "phone") String phone);

    /**
     * @method getUserInfo 获取用户信息
     * @param token token
     * @param key 项目的key
     * @return 用户信息
     **/
    @GetMapping("/facade/token")
    ResponseMessage<User> getUserInfo(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "key") String key);
}
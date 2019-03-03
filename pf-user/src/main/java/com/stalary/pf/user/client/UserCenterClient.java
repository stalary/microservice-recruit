package com.stalary.pf.user.client;

import com.stalary.pf.user.data.dto.ProjectInfo;
import com.stalary.pf.user.data.dto.User;
import com.stalary.pf.user.data.vo.ResponseMessage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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
     * @method updateInfo 更新用户信息
     * @param key 项目的key
     * @param user 用户信息
     * @return token
     **/
    @PostMapping("/token/update")
    ResponseMessage<String> updateInfo(
            @RequestParam(name = "key") String key,
            @RequestBody User user);

    /**
     * @method updatePassword 修改密码
     * @param key 项目的key
     * @param user 用户信息
     * @return token
     **/
    @PostMapping("/token/update/password")
    ResponseMessage<String> updatePassword(
            @RequestParam(name = "key") String key,
            @RequestBody User user);

    /**
     * @method register 用户注册
     * @param key 项目的key
     * @param user 用户信息
     * @return token
     **/
    @PostMapping("/token/register")
    ResponseMessage<String> register(
            @RequestParam(name = "key") String key,
            @RequestBody User user);

    /**
     * @method login 用户登陆
     * @param key 项目的key
     * @param user 用户信息
     * @return token
     **/
    @PostMapping("/token/login")
    ResponseMessage<String> login(
            @RequestParam(name = "key") String key,
            @RequestBody User user);

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

    /**
     * @method getUserInfoById 通过用户id获取用户信息
     * @param userId 用户id
     * @param key 项目的key
     * @param projectId 项目id
     * @return
     **/
    @GetMapping("/facade/user")
    ResponseMessage<User> getUserInfoById(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "key") String key,
            @RequestParam(name = "projectId") Long projectId);
}

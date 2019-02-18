package com.stalary.pf.recruit.client;

import com.stalary.pf.recruit.data.dto.RecommendUser;
import com.stalary.pf.recruit.data.dto.User;
import com.stalary.pf.recruit.data.dto.UserInfo;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2018/12/31
 */
@FeignClient(name = "pf-user")
@Component
public interface UserClient {

    @GetMapping("/user/userId")
    ResponseMessage<User> getUser(@RequestParam("userId") Long userId);

    @GetMapping("/user/recommend")
    ResponseMessage<List<RecommendUser>> getRecommendCandidate(
            @RequestParam("param") String param);

    @GetMapping("/user/info")
    ResponseMessage<UserInfo> getUserInfo(@RequestParam("userId") Long userId);
}

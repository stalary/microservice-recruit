package com.stalary.pf.user.client;

import com.stalary.pf.user.data.dto.Recruit;
import com.stalary.pf.user.data.vo.ResponseMessage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2019/1/1
 */
@FeignClient(name = "pf-recruit")
@Component
public interface RecruitClient {

    @GetMapping("/recruit/list")
    ResponseMessage<List<Recruit>> getRecruitList(@RequestParam("userId") Long userId);
}

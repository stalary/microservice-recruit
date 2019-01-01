package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.Recruit;
import com.stalary.pf.consumer.data.dto.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Stalary
 * @description
 * @date 2019/1/1
 */
@FeignClient(name = "recruit", url = "${gateway.server}")
@Component
public interface RecruitClient {

    @GetMapping("/recruit")
    ResponseMessage<Recruit> getRecruit(@RequestParam("recruitId") Long recruitId);
}

package com.stalary.pf.recruit.client;

import com.stalary.pf.recruit.data.dto.RecruitDto;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Stalary
 * @description
 * @date 2019/1/8
 */
@FeignClient(name = "resume", url = "${resume.server}")
@Component
public interface ResumeClient {

    @PostMapping("/resume/rate")
    ResponseMessage<Integer> getRate(
            @RequestParam("userId") Long userId,
            @RequestBody RecruitDto recruit);
}

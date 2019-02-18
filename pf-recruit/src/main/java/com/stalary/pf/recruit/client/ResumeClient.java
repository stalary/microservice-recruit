package com.stalary.pf.recruit.client;

import com.stalary.pf.recruit.data.dto.GetResumeRate;
import com.stalary.pf.recruit.data.dto.RecruitDto;
import com.stalary.pf.recruit.data.dto.ResumeRate;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2019/1/8
 */
@FeignClient(name = "pf-resume")
@Component
public interface ResumeClient {

    @PostMapping("/resume/rate/batch")
    ResponseMessage<List<ResumeRate>> getRate(@RequestBody GetResumeRate getResumeRate);
}

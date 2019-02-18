package com.stalary.pf.resume.client;

import com.stalary.pf.resume.data.dto.Recruit;
import com.stalary.pf.resume.data.vo.ResponseMessage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/recruit/one")
    ResponseMessage<Recruit> getRecruit(@RequestParam("recruitId") Long recruitId);
}

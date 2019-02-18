package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.SendResume;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Stalary
 * @description
 * @date 2018/12/31
 */
@FeignClient(name = "pf-resume")
@Component
public interface ResumeClient {

    @PostMapping("/resume/handle")
    void handleResume(@RequestBody SendResume sendResume);
}

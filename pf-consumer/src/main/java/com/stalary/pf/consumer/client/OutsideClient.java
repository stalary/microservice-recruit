package com.stalary.pf.consumer.client;

import com.stalary.pf.consumer.data.dto.Email;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Stalary
 * @description
 * @date 2018/12/31
 */
@FeignClient(name = "pf-outside")
@Component
public interface OutsideClient {

    @PostMapping("/outside/email")
    void sendEmail(@RequestBody Email email);
}

/**
 * @(#)FacadeController.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.push.controller;

import com.stalary.pf.push.service.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * FacadeController
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@RestController
public class FacadeController {

    @Resource
    private WebSocketService webSocketService;

    @GetMapping("/send")
    public void sendMessage(
            HttpServletRequest request,
            @RequestParam String message) {
        String userId = request.getHeader("userId");
        webSocketService.sendMessage(Long.valueOf(userId), message);
    }
}
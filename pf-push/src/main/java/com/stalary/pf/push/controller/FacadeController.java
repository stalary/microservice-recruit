/**
 * @(#)FacadeController.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.push.controller;

import com.stalary.pf.push.service.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * FacadeController
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@RestController
@RequestMapping("/push")
public class FacadeController {

    @Resource
    private WebSocketService webSocketService;

    @GetMapping("/send")
    public void sendMessage(
            @RequestParam Long userId,
            @RequestParam String message) {
        webSocketService.messageBroadcast(userId, message);
    }
}
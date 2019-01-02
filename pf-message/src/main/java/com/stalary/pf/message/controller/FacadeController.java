package com.stalary.pf.message.controller;

import com.stalary.pf.message.data.dto.ReadMessage;
import com.stalary.pf.message.data.entity.MessageEntity;
import com.stalary.pf.message.data.vo.ResponseMessage;
import com.stalary.pf.message.holder.UserHolder;
import com.stalary.pf.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * FacadeController
 *
 * @author lirongqian
 * @description 站内信接口
 * @since 2018/04/17
 */
@RestController
@Slf4j
@RequestMapping("/message")
public class FacadeController {

    @Resource
    private MessageService messageService;

    /**
     * @param message 站内信
     * @return Message 站内信
     * @method postMessage 推送一条站内信，可以供前端群发站内信使用
     **/
    @PostMapping
    public ResponseMessage postMessage(
            @RequestBody MessageEntity message) {
        log.info("message: " + message);
        return ResponseMessage.successMessage(messageService.save(message));
    }

    /**
     * @return Message 站内信
     * @method getMessage 获取一个用户接收的站内信
     **/
    @GetMapping("/user/get")
    public ResponseMessage getMessage() {
        Long userId = UserHolder.get();
        return ResponseMessage.successMessage(messageService.findByToId(userId));
    }

    /**
     * @param readMessage 已读对象
     * @method readMessage 已读站内信
     **/
    @PostMapping("/read")
    public ResponseMessage readMessage(
            @RequestBody ReadMessage readMessage) {
        messageService.read(readMessage.getId(), readMessage.getUserId());
        return ResponseMessage.successMessage();
    }


    /**
     * @return Message 站内信
     * @method getSendMessage 获取一个用户发送的站内信
     **/
    @GetMapping("/user/send")
    public ResponseMessage getSendMessage() {
        Long userId = UserHolder.get();
        return ResponseMessage.successMessage(messageService.findByFromId(userId));
    }

    @GetMapping("/sendCount")
    public ResponseMessage sendCount(
            @RequestParam Long userId) {
        messageService.sendCount(userId);
        return ResponseMessage.successMessage();
    }
}
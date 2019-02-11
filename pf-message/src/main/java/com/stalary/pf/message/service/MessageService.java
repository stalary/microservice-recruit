/**
 * @(#)MessageService.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.message.service;

import com.stalary.pf.message.client.PushClient;
import com.stalary.pf.message.data.entity.MessageEntity;
import com.stalary.pf.message.repo.MessageRepo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * MessageService
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@Service
public class MessageService {

    @Resource(name = "messageRepo")
    private MessageRepo repo;

    @Resource
    private PushClient pushClient;

    public List<MessageEntity> findByToId(Long toId) {
        return repo.findByToIdOrderByCreateTimeDesc(toId);
    }

    public List<MessageEntity> findByFromId(Long fromId) {
        return repo.findByFromId(fromId);
    }

    public MessageEntity save(MessageEntity entity) {
        return repo.save(entity);
    }

    /**
     * 查找未读通知的数量
     **/
    public List<MessageEntity> findNotRead(Long userId) {
        return repo.findByToIdAndReadState(userId, false);
    }

    public void read(Long id, Long userId) {
        repo.read(id);
        int count = findNotRead(userId).size();
        pushClient.sendMessage(userId, String.valueOf(count));
    }

    public void sendCount(Long userId) {
        int count = findNotRead(userId).size();
        pushClient.sendMessage(userId, String.valueOf(count));
    }
}
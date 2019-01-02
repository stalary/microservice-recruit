/**
 * @(#)Consumer.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.consumer.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.lightmqclient.MQListener;
import com.stalary.lightmqclient.MessageDto;
import com.stalary.lightmqclient.facade.MQConsumer;
import com.stalary.pf.consumer.client.*;
import com.stalary.pf.consumer.data.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.stalary.pf.consumer.data.constant.Constant.*;


/**
 * Consumer
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@Service
@Slf4j
public class Consumer implements MQConsumer {

    private static ResumeClient resumeClient;

    @Autowired
    public void setResumeClient(ResumeClient resumeClient) {
        Consumer.resumeClient = resumeClient;
    }

    private static PushClient pushClient;

    @Autowired
    public void setPushClient(PushClient pushClient) {
        Consumer.pushClient = pushClient;
    }

    private static MessageClient messageClient;

    @Autowired
    public void setMessageClient(MessageClient messageClient) {
        Consumer.messageClient = messageClient;
    }

    private static RecruitClient recruitClient;

    @Autowired
    private void setRecruitClient(RecruitClient recruitClient) {
        Consumer.recruitClient = recruitClient;
    }

    private static UserClient userClient;

    @Autowired
    private void setUserClient(UserClient userClient) {
        Consumer.userClient = userClient;
    }

    private static OutsideClient outsideClient;

    @Autowired
    private void setOutsideClient(OutsideClient outsideClient) {
        Consumer.outsideClient = outsideClient;
    }

    @Override
    @MQListener(topics = {SEND_RESUME, RECEIVE_RESUME, HANDLE_RESUME})
    public void process(MessageDto messageDto) {
        long startTime = System.currentTimeMillis();
        String topic = messageDto.getTopic();
        String key = "";
        if (messageDto.getKey() != null) {
            key = messageDto.getKey();
        }
        String message = messageDto.getValue();
        Long offset = messageDto.getOffset();
        log.info("receive message: topic: " + topic + " key: " + key + " message: " + message + " offset: " + offset);
        if (HANDLE_RESUME.equals(topic)) {
            SendResume resume = JSONObject.parseObject(message, SendResume.class);
            // 处理投递简历
            resumeClient.handleResume(resume);
        } else if (SEND_RESUME.equals(topic)) {
            SendResume resume = JSONObject.parseObject(message, SendResume.class);
            // 存储投递的消息通知(系统发送)
            Long userId = resume.getUserId();
            Message m = new Message(0L, userId, "简历投递成功", resume.getTitle() + "简历投递成功", false);
            messageClient.saveMessage(m);
            // 统计通知未读的数量
            int count = messageClient.getNotReadCount(userId).getData();
            pushClient.sendMessage(userId, "" + count);
        } else if (RECEIVE_RESUME.equals(topic)) {
            SendResume resume = JSONObject.parseObject(message, SendResume.class);
            // 存储收到简历的消息通知(系统发送)
            Long recruitId = resume.getRecruitId();
            Long userId = resume.getUserId();
            Recruit recruit = recruitClient.getRecruit(recruitId).getData();
            UserInfo userInfo = userClient.getUserInfo(userId).getData();
            Long hrId = recruit.getHrId();
            Message m = new Message(0L, hrId, resume.getTitle() + "收到简历", resume.getTitle() + "收到来自" + userInfo.getSchool() + "的" + userInfo.getNickname() + "的简历", false);
            messageClient.saveMessage(m);
            outsideClient.sendEmail(new Email(userClient.getEmail(hrId).getData(), "收到投递简历", resume.getTitle() + "收到来自" + userInfo.getSchool() + "的" + userInfo.getNickname() + "的简历"));
            // 统计通知未读的数量
            int count = messageClient.getNotReadCount(hrId).getData();
            pushClient.sendMessage(hrId, "" + count);
        }
        long endTime = System.currentTimeMillis();
        log.info("Consumer.time=" + (endTime - startTime));
    }

}
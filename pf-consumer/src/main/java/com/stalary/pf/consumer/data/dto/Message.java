package com.stalary.pf.consumer.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @model Message
 * @description 站内信对象
 * @field id id
 * @field fromId 发送方id，系统发送则id为0
 * @field toId 接收方id
 * @field title 站内信标题
 * @field content 内容
 * @field readState 是否已阅
 * @field updateTime 最后更新时间
 * @field createTime 创建时间
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Long fromId;

    private Long toId;

    private String title;

    private String content;

    private Boolean readState = false;
}
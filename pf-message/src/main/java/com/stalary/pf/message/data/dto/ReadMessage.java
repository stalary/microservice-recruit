package com.stalary.pf.message.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model ReadMessage
 * @description 已读对象
 * @field id 站内信id
 * @field userId 用户id
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadMessage {

    private Long id;

    private Long userId;
}
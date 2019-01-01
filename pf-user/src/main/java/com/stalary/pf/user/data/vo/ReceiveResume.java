package com.stalary.pf.user.data.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @model ReceiveResume
 * @description 接收简历对象
 * @field title 岗位标题
 * @field name 用户昵称
 * @field userId 用户id
 * @field rate 简历匹配度
 * @field time 接收时间
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveResume {

    private String title;

    private String name;

    private Long userId;

    private Integer rate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
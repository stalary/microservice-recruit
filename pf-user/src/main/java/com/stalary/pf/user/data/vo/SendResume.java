package com.stalary.pf.user.data.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @model SendResume
 * @description 投递简历对象
 * @field userId 用户id
 * @field recruitId 岗位id
 * @field title 岗位标题
 * @field time 投递时间
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendResume {

    private Long userId;

    private Long recruitId;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
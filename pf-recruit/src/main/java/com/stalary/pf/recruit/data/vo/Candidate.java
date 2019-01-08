/**
 * @(#)Candidate.java, 2019-01-08.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.recruit.data.vo;

import com.stalary.pf.recruit.data.dto.UserInfo;
import lombok.Data;

/**
 * @model Candidate
 * @description 推荐候选人信息
 * @field userId 用户id
 * @field name 姓名
 * @field school 学校
 * @field rate 匹配度
 * @author lirongqian
 * @since 2019/01/08
 */
@Data
public class Candidate {

    private Long userId;

    private String name;

    private String school;

    private Integer rate;

    public static Candidate init(UserInfo userInfo) {
        Candidate candidate = new Candidate();
        candidate.name = userInfo.getName();
        candidate.userId = userInfo.getUserId();
        candidate.school = userInfo.getSchool();
        return candidate;
    }
}
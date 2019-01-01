package com.stalary.pf.resume.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model UserInfo
 * @description 用户信息对象
 * @field userId 用户id
 * @field nickname 昵称
 * @field username 用户名
 * @field sex 性别
 * @field introduce 个人介绍
 * @field address 地址
 * @field intentionCompany 意向公司
 * @field endTime 毕业时间
 * @field school 毕业学校
 * @field education 学历
 * @field intentionJob 意向岗位
 * @field avatar 头像
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long userId;

    private String username;

    private String nickname;

    private String sex;

    private String introduce;

    private String address;

    private String intentionCompany;

    private Integer endTime;

    private String school;

    private String education;

    private String intentionJob;

    private String avatar;
}
package com.stalary.pf.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model Applicant
 * @description 求职对象
 * @field username 用户名
 * @field phone 手机号
 * @field password 密码
 * @field email 邮箱
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {

    private String username;

    private String phone;

    private String password;

    private String email;

    private String code;
}
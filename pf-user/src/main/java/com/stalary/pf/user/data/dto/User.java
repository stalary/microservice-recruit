package com.stalary.pf.user.data.dto;

import lombok.Data;

/**
 * @model User
 * @description 用户对象
 * @field userId 用户id
 * @field nickname 昵称
 * @field username 用户名
 * @field password 密码
 * @field companyId 关联的公司Id
 * @field phone 手机号
 * @field email 邮箱
 * @field projectId 项目id
 * @field role 角色，1为hr，2为求职者
 * @field firstId 关联Id
 * @field code 验证码(注册时使用)
 **/
@Data
public class User {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String password;

    private String email;

    private Long projectId;

    private Integer role;

    private Long firstId;

    private String code;

    public User(Applicant applicant) {
        this.username = applicant.getUsername();
        this.password = applicant.getPassword();
        this.phone = applicant.getPhone();
        this.email = applicant.getEmail();
        this.code = applicant.getCode();
        this.role = 2;
    }

    public User(HR hr) {
        this.username = hr.getUsername();
        this.nickname = hr.getNickname();
        this.password = hr.getPassword();
        this.phone = hr.getPhone();
        this.email = hr.getEmail();
        this.firstId = hr.getCompanyId();
        this.code = hr.getCode();
        this.role = 1;
    }

    public User() {
    }
}
package com.stalary.pf.user.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @model UserInfo
 * @description 用户信息对象
 * @field id id
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
 * @field updateTime 最后更新时间
 * @field createTime 创建时间
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userInfo")
@Entity
public class UserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @JsonIgnore
    @UpdateTimestamp
    private Date updateTime;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
package com.stalary.pf.resume.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

/**
 * @model Resume
 * @description 简历对象
 * @field userId 关联的用户id
 * @field name 姓名
 * @field sex 性别
 * @field age 年龄
 * @field skills 技能(见Skill)
 * @field school 毕业院校
 * @field address 地址
 * @field endTime 毕业时间，仅需要精准到年
 * @field phone 联系电话
 * @field email 邮箱
 * @field introduce 自我介绍
 * @field experience 个人经历：包括项目经验等
 * @field awards 奖项
 * @field avatar 头像
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Resume extends BaseEntity {

    private Long userId;

    private String name;

    private String sex;

    private int age;

    @DBRef
    private List<Skill> skills;

    private String school;

    private String address;

    private int endTime;

    private String phone;

    private String email;

    private String introduce;

    private String experience;

    private String awards;

    private String avatar;
}
package com.stalary.pf.user.data.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @model UserInfo
 * @description 用户信息对象
 * @field id id
 * @field userId 用户id
 * @field name 姓名
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
@Document(indexName = "user", type = "user")
public class UserEs {

    @Id
    private Long userId;

    private String name;

    private String school;

    private String education;

    private String intentionCompany;

    private String intentionJob;

    public UserEs(UserInfoEntity entity) {
        this.userId = entity.getUserId();
        this.name = entity.getName();
        this.school = entity.getSchool();
        this.education = entity.getEducation();
        this.intentionCompany = entity.getIntentionCompany();
        this.intentionJob = entity.getIntentionJob();
    }

    public UserEs(Long userId, String name, String school, String education, String intentionCompany, String intentionJob) {
        this.userId = userId;
        this.name = name;
        this.school = school;
        this.education = education;
        this.intentionCompany = intentionCompany;
        this.intentionJob = intentionJob;
    }

    public UserEs() {
    }
}
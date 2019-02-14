package com.stalary.pf.recruit.data.dto;

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
 * @field userId 用户id
 * @field name 姓名
 * @field sex 性别
 * @field intentionCompany 意向公司
 * @field school 毕业学校
 * @field education 学历
 * @field intentionJob 意向岗位
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long userId;

    private String name;

    private String school;

    private String education;

    private String intentionCompany;

    private String intentionJob;
}
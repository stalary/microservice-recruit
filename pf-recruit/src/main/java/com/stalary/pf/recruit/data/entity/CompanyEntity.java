package com.stalary.pf.recruit.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @model CompanyEntity
 * @description 公司对象
 * @field name 名称
 * @field address 地址
 * @field avatar 头像
 * @field introduce 简介
 * @field scale 规模
 * @field type 类型
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "company")
@Entity
public class CompanyEntity extends BaseEntity {

    private String name;

    private String address;

    private String avatar;

    private String introduce;

    private String scale;

    private String type;
}
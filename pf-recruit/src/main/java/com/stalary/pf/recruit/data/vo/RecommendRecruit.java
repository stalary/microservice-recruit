/**
 * @(#)RecommendRecruit.java, 2019-01-08.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.recruit.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @model RecommendRecruit
 * @description 推荐职位
 * @field recruitId 职位id
 * @field title 标题
 * @field companyName 公司名称
 * @author lirongqian
 * @since 2019/01/08
 */
@Data
@AllArgsConstructor
public class RecommendRecruit {

    private Long recruitId;

    private String title;

    private String companyName;
}
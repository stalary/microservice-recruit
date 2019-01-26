/**
 * @(#)RecruitDto.java, 2019-01-20.
 *
 * Copyright 2019 Youdao, Inc. All rights reserved.
 * YOUDAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.stalary.pf.recruit.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RecruitDto
 *
 * @author lirongqian
 * @since 2019/01/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitDto {

    private Long id;

    private List<SkillRule> skillList;
}
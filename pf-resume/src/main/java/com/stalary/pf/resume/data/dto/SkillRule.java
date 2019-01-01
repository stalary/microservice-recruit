package com.stalary.pf.resume.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SkillRule
 * 技能规则表
 * @author lirongqian
 * @since 2018/04/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillRule {

    private String name;

    private Integer weight;
}
package com.stalary.pf.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * @model Recruit
 * @description 招聘信息
 * @field id id
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recruit {

    private Long id;

    private List<SkillRule> skillList;
}
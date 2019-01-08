/**
 * @(#)RecommendCandidate.java, 2019-01-08.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.recruit.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @model RecommendCandidate
 * @description 推荐候选人信息
 * @field title 职位
 * @field candidateList 候选人列表
 * @author lirongqian
 * @since 2019/01/08
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class RecommendCandidate {

    private String title;

    private List<Candidate> candidateList;
}
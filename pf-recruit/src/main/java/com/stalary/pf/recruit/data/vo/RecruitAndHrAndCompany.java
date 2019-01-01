package com.stalary.pf.recruit.data.vo;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model RecruitAndHrAndCompany
 * @description 招聘详细信息对象
 * @field recruit 招聘信息(见Recruit)
 * @field hr 招聘信息(见HR)
 * @field company 招聘信息(见Company)
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitAndHrAndCompany {

    private RecruitEntity recruit;

    private HR hr;

    private CompanyEntity company;
}
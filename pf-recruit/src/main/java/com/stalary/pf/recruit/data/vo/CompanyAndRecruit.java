
package com.stalary.pf.recruit.data.vo;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @model CompanyAndRecruit
 * @description 公司详情
 * @field company 公司(见Company)
 * @field recruitList 招聘信息列表(见Recruit)
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAndRecruit {

    private CompanyEntity company;

    private List<RecruitEntity> recruitList;
}
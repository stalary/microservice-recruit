
package com.stalary.pf.recruit.data.vo;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RecruitAndCompany
 *
 * @author lirongqian
 * @since 2018/04/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitAndCompany {

    private RecruitEntity recruit;

    private CompanyEntity company;
}
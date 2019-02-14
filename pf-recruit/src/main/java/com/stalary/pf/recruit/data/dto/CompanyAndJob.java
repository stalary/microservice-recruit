
package com.stalary.pf.recruit.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CompanyAndJob
 *
 * @author lirongqian
 * @since 2019/02/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAndJob {

    private Long recruitId;

    private String company;

    private String job;
}
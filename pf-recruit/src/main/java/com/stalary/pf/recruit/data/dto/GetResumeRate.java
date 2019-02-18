
package com.stalary.pf.recruit.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GetResumeRate
 *
 * @author lirongqian
 * @since 2019/02/14
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetResumeRate {

    private List<GetRate> getList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetRate {

        private Long userId;

        private RecruitDto recruit;
    }
}
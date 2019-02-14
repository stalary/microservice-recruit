
package com.stalary.pf.resume.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResumeRate
 *
 * @author lirongqian
 * @since 2019/02/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRate {
    
    private Long userId;
    
    private Long recruitId;

    private Integer rate;
}
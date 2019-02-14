/**
 * @(#)GetResumeRate.java, 2019-02-14.
 *
 * Copyright 2019 Youdao, Inc. All rights reserved.
 * YOUDAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.stalary.pf.resume.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

        private Recruit recruit;
    }
}
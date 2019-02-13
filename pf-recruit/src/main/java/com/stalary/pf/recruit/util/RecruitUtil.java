/**
 * @(#)UserUtil.java, 2019-01-03.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.recruit.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.http.HttpServletRequest;

/**
 * RecruitUtil
 *
 * @author lirongqian
 * @since 2019/01/03
 */
public class RecruitUtil {

    public static Long getUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        if (StringUtils.isNotEmpty(userId)) {
            return Long.valueOf(userId);
        }
        return null;
    }

    public static Pair<Integer, Integer> getStartAndEnd(int page, int size) {
        int start = (page - 1) * size;
        int end = start + size;
        return Pair.of(start, end);
    }

}
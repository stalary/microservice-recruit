/**
 * @(#)UserUtil.java, 2019-01-03.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.message.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * UserUtil
 *
 * @author lirongqian
 * @since 2019/01/03
 */
public class UserUtil {

    public static Long getUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        if (StringUtils.isNotEmpty(userId)) {
            return Long.valueOf(userId);
        }
        return null;
    }
}
/**
 * @(#)Constant.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.outside.data;

import com.google.common.base.Joiner;

/**
 * Constant
 *
 * @author lirongqian
 * @since 2018/12/31
 */
public class Constant {

    public static final String SPLIT = ":";

    public static final String OK = "OK";

    public static final String USER_AVATAR = "avatar";

    public static final String PHONE_CODE = "phone_code";

    public static final String RECEIVE_RESUME = "receive_resume";

    public static final Joiner JOINER = Joiner.on(SPLIT);

    public static String getKey(String... keys) {
        return JOINER.join(keys);
    }
}
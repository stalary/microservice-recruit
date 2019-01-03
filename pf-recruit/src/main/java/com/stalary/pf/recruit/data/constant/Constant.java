package com.stalary.pf.recruit.data.constant;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Constant
 * 存储常量
 * @author lirongqian
 * @since 2018/04/14
 */
public class Constant {

    public static final String SPLIT = ":";

    /**
     * 投递简历
     */
    public static final String SEND_RESUME = "send_resume";

    /**
     * 处理简历
     */
    public static final String HANDLE_RESUME = "handle_resume";

    /**
     * 接受方
     */
    public static final String RECEIVE_RESUME = "receive_resume";

    public static final Joiner JOINER = Joiner.on(SPLIT);

    public static String getKey(String... keys) {
        return JOINER.join(keys);
    }
}
package com.stalary.pf.resume.data.constant;

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


    public static final Joiner JOINER = Joiner.on(SPLIT);

    public static String getKey(String... keys) {
        return JOINER.join(keys);
    }
}
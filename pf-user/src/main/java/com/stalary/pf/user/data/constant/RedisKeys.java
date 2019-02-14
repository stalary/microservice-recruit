package com.stalary.pf.user.data.constant;

/**
 * RedisKeys
 *
 * @author lirongqian
 * @since 2018/04/20
 */
public class RedisKeys {

    /** token:user缓存 **/
    public static final String USER_TOKEN = "user_token";

    /** userId:user缓存 **/
    public static final String USER_ID = "user_id";

    /** 手机验证码缓存 **/
    public static final String PHONE_CODE = "phone_code";

    /** 投递列表缓存 **/
    public static final String RESUME_SEND = "resume_send";

    /** 简历获取列表缓存 **/
    public static final String RESUME_RECEIVE = "resume_receive";
}
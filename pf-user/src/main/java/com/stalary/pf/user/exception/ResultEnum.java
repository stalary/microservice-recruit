package com.stalary.pf.user.exception;

import lombok.Getter;

/**
 * @author Stalary
 * @description
 * @date 2018/03/24
 */
public enum ResultEnum {
    UNKNOW_ERROR(500, "服务器错误"),

    // 1开头为用户有关的错误
    NEED_LOGIN(1001, "未登陆"),

    CODE_ERROR(1002, "短信验证码错误"),

    CODE_EXPIRE(1003, "短信验证码已过期，请重新输入"),

    // 2开头为第三方接口的错误
    SEND_NOTE_ERROR(2001, "发送短信失败"),

    QINIU_ERROR(2002, "七牛云接口出错"),

    SUCCESS(0, "成功");

    @Getter
    private Integer code;

    @Getter
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}

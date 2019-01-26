package com.stalary.pf.outside.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * MyException
 *
 * @author lirongqian
 * @since 2018/04/14
 */
@Slf4j
public class MyException extends RuntimeException {

    @Getter
    private Integer code;

    public MyException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        log.warn("exception! {}", resultEnum.getMsg());
    }

    public MyException(Integer code,  String msg) {
        super(msg);
        this.code = code;
        log.warn("exception! {}", msg);
    }

}
package com.stalary.pf.user.exception;

import com.stalary.pf.user.data.vo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Stalary
 * @description
 * @date 2018/04/14
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseMessage handle(Exception e) {
        if (e instanceof MyException) {
            MyException myException = (MyException) e;
            return ResponseMessage.error(myException.getCode(), myException.getMessage());
        } else {
            log.error("[系统异常] {}", e);
            return ResponseMessage.error(666, "运行时异常！");
        }
    }
}

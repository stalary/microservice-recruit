package com.stalary.pf.outside.exception;

import com.stalary.pf.outside.data.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

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
            log.error("[系统异常]", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return ResponseMessage.error(500, "outside 运行时异常！" + sw.toString());
        }
    }
}

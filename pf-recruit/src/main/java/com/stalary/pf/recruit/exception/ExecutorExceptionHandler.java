
package com.stalary.pf.recruit.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * ExecutorExceptionHandler
 * 线程池异常捕获
 * @author lirongqian
 * @since 2019/02/12
 */
@Slf4j
public class ExecutorExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("thread: {}, exception: ", t.getName(), e);
    }
}
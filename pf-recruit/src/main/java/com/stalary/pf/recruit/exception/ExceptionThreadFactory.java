package com.stalary.pf.recruit.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExceptionThreadFactory
 *
 * @author lirongqian
 * @since 2019/02/12
 */
@Slf4j
public class ExceptionThreadFactory implements ThreadFactory {

    private String namePrefix;

    private AtomicInteger id = new AtomicInteger(0);

    private boolean daemon;

    public ExceptionThreadFactory(String name, boolean daemon) {
        this.namePrefix = name;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + "-" + id.addAndGet(1));
        t.setDaemon(daemon);
        // 设置异常处理类
        t.setUncaughtExceptionHandler(new ExecutorExceptionHandler());
        return t;
    }
}
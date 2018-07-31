package com.adzuki.worker.base.executor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础定时任务的线程被拒绝记录日志
 */
public class DiscardPolicyWithBaseScheduleLog extends DiscardPolicy {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscardPolicyWithBaseScheduleLog.class);

    static final AtomicInteger rejectedLogTaskCount = new AtomicInteger(0);

    private String threadName;

    public DiscardPolicyWithBaseScheduleLog(){

    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        int counts = rejectedLogTaskCount.incrementAndGet();
        String msg = String.format("Thread pool 耗尽了!" +
                " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d), Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s!" ,
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating(), "");
        logger.info(msg);
        logger.info("基础定时任务队列已满，任务被拒绝！拒绝总数：" + counts + "，任务：" + r.toString());
        super.rejectedExecution(r, e);
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}

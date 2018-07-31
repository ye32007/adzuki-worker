package com.adzuki.worker.base.executor;


import java.util.List;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.adzuki.worker.base.service.TaskExcuteService;
import com.adzuki.worker.base.thread.SimpleWorkerThread;


/**
 * 调度执行接口
 */
public abstract class AbstractScheduleExecutor implements ScheduleExecutor{
	
    /**
     * 池化处理
     * @param threadPoolTaskExecutor
     * @param standardTaskService
     * @param list
     */
    protected void setSimpleWorkerThreadList(ThreadPoolTaskExecutor threadPoolTaskExecutor,TaskExcuteService standardTaskService,List list){
        SimpleWorkerThread workerThread = new SimpleWorkerThread();
        workerThread.setTaskList(list);
        workerThread.setSimpleTaskService(standardTaskService);
        threadPoolTaskExecutor.execute(workerThread);
    }

}


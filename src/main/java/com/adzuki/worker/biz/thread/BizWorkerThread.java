package com.adzuki.worker.biz.thread;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adzuki.worker.base.executor.DiscardPolicyWithBaseScheduleLog;
import com.adzuki.worker.biz.dispatcher.TaskDispatcher;
import com.adzuki.worker.cache.TaskServiceConfigCache;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.manager.TaskManager;

/**
 */
public class BizWorkerThread<T> implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscardPolicyWithBaseScheduleLog.class);

    private TaskManager taskManager;

    private TaskDispatcher taskDispatcher;
    private List<T> list;

    @Resource(name = "taskServiceConfigCache")
    TaskServiceConfigCache taskServiceConfigCache;
    
    @Override
    public void run() {
    	List<T> lockedList = new ArrayList<T>();
    	logger.info("锁定任务表开始，预估锁定数量为{}", list.size());
    	long lockStartTime = System.currentTimeMillis();
    	for (T t : list) {
    		long taskStartTime = System.currentTimeMillis();
            try {
                //任务锁定
                boolean lockResult = taskManager.lock((Task)t);
                //锁定失败进行下一个
                if (!lockResult) {
                	logger.error(((Task)t).getUuid()+"锁定失败");
                    continue;
                }
                lockedList.add(t);
            } catch (Exception e) {
                logger.error(((Task)t).getUuid()+"锁定异常", e);
            } finally {
                long taskEndTime = System.currentTimeMillis() - taskStartTime;
                logger.info( "执行任务"+((Task)t).getUuid()+"锁定耗时：" + taskEndTime+"ms");
            }
        }
    	long lockEndTime = System.currentTimeMillis() - lockStartTime;
    	logger.info("锁定任务表完成，实际锁定数量为{}，总耗时{}", lockedList.size(), lockEndTime/1000);
        for (T t : lockedList) {
            long startTime = System.currentTimeMillis();
            try {
                boolean resultProcess = taskDispatcher.dispatch((Task)t);//任务执行
                if(resultProcess){//如果执行成功，则更改任务状态为执行完成
                	taskManager.finish((Task)t);
                }
            } catch (Exception e) {
                logger.error(((Task)t).getUuid()+"BizWorkerThread run is error", e);
            } finally {
                long endTime = System.currentTimeMillis() - startTime;
                logger.info( "执行线程"+((Task)t).getUuid()+"耗时" + (endTime / 1000));
            }
        }
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setQueue(List<T> list) {
        this.list = list;
    }

    public void setTaskDispatcher(TaskDispatcher taskDispatcher) {
        this.taskDispatcher = taskDispatcher;
    }
}

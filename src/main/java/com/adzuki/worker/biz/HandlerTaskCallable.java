package com.adzuki.worker.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adzuki.worker.biz.dispatcher.TaskDispatcher;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.manager.TaskManager;

/**
 */
public class HandlerTaskCallable implements Callable<List<TaskHandlerResult>> {
	
    private static final Logger logger = LoggerFactory.getLogger(HandlerTaskCallable.class);
    
    private List<Task> taskQueue;
    private TaskManager taskManager;
    private TaskDispatcher taskDispatcher;
    
    @Override
    public List<TaskHandlerResult> call() throws Exception {
        List<TaskHandlerResult> taskHandlerResultList = null;//记录执行结果
        if(taskQueue!=null) {
            taskHandlerResultList = new ArrayList<TaskHandlerResult>();
            for (Task task : taskQueue) {
                TaskHandlerResult taskHandlerResult = new TaskHandlerResult();
                try {
               /* TaskServiceConfig taskServiceConfig = taskServiceConfigCache.taskServiceConfig_taskType_Map.get(((Task) t).getTaskType());
                if(taskServiceConfig==null||taskServiceConfig.getStatus().intValue()==0){//不存在或者没有启动，继续锁定
                    return;
                }*/
                    //任务锁定
                    boolean lockResult = taskManager.lock(task);//①
                    //锁定失败进行下一个
                    if (!lockResult) {//②
                        taskHandlerResult.setResult(null);
                        taskHandlerResult.setResultmsg("该任务已经被其他机器锁定");
                        continue;
                    }
                    boolean resultProcess = taskDispatcher.dispatch(task);//任务执行//③
                    if (resultProcess) {//如果执行成功，则更改任务状态为执行完成
                        taskHandlerResult.setResult(Boolean.TRUE);
                        boolean finishResult = taskManager.finish(task);//④
                        if(!finishResult){
                            taskHandlerResult.setResult(Boolean.FALSE);
                            taskHandlerResult.setResultmsg("执行任务成功，但事后更改状态为完成失败");
                        }
                    }else{//一般不会执行到这一步，会直接抛到异常
                        taskHandlerResult.setResult(Boolean.FALSE);
                        taskHandlerResult.setResultmsg("执行任务失败");//目前没有返回详细说明
                    }
                } catch (Exception e) {
                	logger.error("BizWorkerThread run is error", e);
                    taskHandlerResult.setResult(Boolean.FALSE);
                    taskHandlerResult.setResultmsg("执行任务出现异常:"+e.getMessage());
                }finally {
                    taskHandlerResultList.add(taskHandlerResult);
                }
            }
        }
        return taskHandlerResultList;
    }

    public List<Task> getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(List<Task> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskDispatcher getTaskDispatcher() {
        return taskDispatcher;
    }

    public void setTaskDispatcher(TaskDispatcher taskDispatcher) {
        this.taskDispatcher = taskDispatcher;
    }
}
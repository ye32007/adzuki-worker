package com.adzuki.worker.biz.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.adzuki.worker.biz.HandlerTaskCallable;
import com.adzuki.worker.biz.TaskHandlerResult;
import com.adzuki.worker.biz.dispatcher.TaskDispatcher;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.enums.TaskEnum;
import com.adzuki.worker.manager.TaskManager;
import com.adzuki.worker.util.WorkerUtil;


@Service("bizTaskExecutor")
public class BizScheduleExecutor  {
	
	private static final Logger logger = LoggerFactory.getLogger(BizScheduleExecutor.class);
	
	@Autowired
	private ThreadPoolTaskExecutor bizTaskThreadPool;
    @Resource(name = "taskDispatcherService")
	private TaskDispatcher taskDispatcherService;
    @Resource(name = "taskManager")
    private TaskManager taskManager;
    @Value("${schedule.biz.batchSize:10}")
    public Integer batchSize;

    public void execute() throws Exception {
        try {
            List<Task> listSubTask = taskManager.findFromDate(TaskEnum.TaskStatus.init.status(), new Date());
            if(listSubTask!=null && listSubTask.size()>0){
                int listSize = listSubTask.size();
                int batchTimes = WorkerUtil.computeBatchTimes(listSize,
                        batchSize);
                List<Future<List<TaskHandlerResult>>> taskFutureResultList = new ArrayList<Future<List<TaskHandlerResult>>>();
                for (int i = 0; i < batchTimes; i++) {
                    int from = WorkerUtil.computeBatchFromIndex(listSize,  batchSize, i);
                    int to = WorkerUtil.computeBatchToIndex(listSize,  batchSize, i);
                    HandlerTaskCallable handlerTaskCallable = new HandlerTaskCallable();
                    handlerTaskCallable.setTaskDispatcher(taskDispatcherService);
                    handlerTaskCallable.setTaskManager(taskManager);
                    handlerTaskCallable.setTaskQueue(listSubTask.subList(from, to));//每个callable执行数据的条数是batchSize
                    Future<List<TaskHandlerResult>> taskHandlerResultFuture =  bizTaskThreadPool.submit(handlerTaskCallable);
                    taskFutureResultList.add(taskHandlerResultFuture);
                }
//                for(Future<List<TaskHandlerResult>> taskHandlerResultFuture : taskFutureResultList){
//                	//TODO
//                   // List<TaskHandlerResult> taskHandlerResultList = taskHandlerResultFuture.get(2, TimeUnit.SECONDS);
//                }
            }
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException){
            	logger.error("BizScheduleExecutor "+" data is null:"+e);
            }else{
            	logger.error("BizScheduleExecutor :",e);
            }
            throw e;
        }
    }
}

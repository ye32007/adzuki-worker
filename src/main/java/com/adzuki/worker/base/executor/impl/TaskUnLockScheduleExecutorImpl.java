package com.adzuki.worker.base.executor.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.executor.AbstractScheduleExecutor;
import com.adzuki.worker.base.service.TaskExcuteService;
import com.adzuki.worker.base.thread.SimpleWorkerThread;
import com.adzuki.worker.constants.Constants;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.manager.TaskManager;
import com.adzuki.worker.util.WorkerUtil;

/**
 * 任务调度--定期重置被锁定的任务
 *
 */
@Service("taskUnlockExecutor")
public class TaskUnLockScheduleExecutorImpl extends AbstractScheduleExecutor {

	private static final Logger logger = LoggerFactory.getLogger(TaskUnLockScheduleExecutorImpl.class);
	
	/**配置任务最大锁定时间（秒）*/
	@Value("${schedule.unlock.splitSeconds:300}")
	private Integer splitSeconds;
	/**配置失败重试次数*/
	@Value("${schedule.unlock.retryCount:100}")
	private Integer retryCount;
	/** 配置每次批处理个数 */
	@Value("${schedule.unlock.batchSize:50}")
	private Integer batchSize;
	/** 线程池 */
    @Autowired
	private ThreadPoolTaskExecutor taskunLockThreadPool;
	/** 失败重置服务层*/
    @Autowired
    private TaskExcuteService taskUnLockService;
    @Autowired
    protected TaskManager taskManager;

	@Override
	public void execute() throws Exception {
		try {
			//如果没有配置则使用默认值
			if(splitSeconds==null){
                splitSeconds = Constants.TASK_CONFIG_TASKLOCK_DEFAULT_MAXSECONDS;
			}
			if(retryCount==null){
				retryCount = Constants.TASK_CONFIG_DEFAULT_RETRYCOUNT;
			}
			if (batchSize == null) {
				batchSize = Constants.TASK_BATCH_DEFAULT_SIZE;
			}
            Date date = new Date();
            date = new Date(date.getTime() - splitSeconds.intValue() * 1000);//拿splitSeconds秒前锁定的数据
			List<Task> lockTaskList = taskManager.selectLockedTask(date, retryCount);
			if(lockTaskList!=null&&lockTaskList.size()>0){
				int listSize = lockTaskList.size();
				int batchTimes = WorkerUtil.computeBatchTimes(listSize, batchSize);
				for (int i = 0; i < batchTimes; i++) {
					SimpleWorkerThread swt = new SimpleWorkerThread();
					int from = WorkerUtil.computeBatchFromIndex(listSize, batchSize, i);
					int to = WorkerUtil.computeBatchToIndex(listSize, batchSize, i);
					swt.setTaskList(lockTaskList.subList(from,to));
					swt.setSimpleTaskService(taskUnLockService);
                    this.setSimpleWorkerThreadList(taskunLockThreadPool,taskUnLockService,lockTaskList.subList(from,to));
				}
			}
		} catch (Exception e) {
			logger.error("子任务调度--定期重置被锁定的子任务异常:"+e);
            throw e;
		}
	}
}

package com.adzuki.worker.base.executor.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.executor.AbstractScheduleExecutor;
import com.adzuki.worker.base.service.TaskExcuteService;
import com.adzuki.worker.base.thread.SimpleWorkerThread;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.manager.TimerTaskConfigManager;
import com.adzuki.worker.util.WorkerUtil;

/**
 * 任务调度--生成定时任务
 *
 */
@Service("timerTaskExecutor")
public class TimerTaskExecutorImpl extends AbstractScheduleExecutor {

	private static final Logger logger = LoggerFactory.getLogger(TimerTaskExecutorImpl.class);

	/** 配置每次批处理个数 */
	@Value("${schedule.base.batchSize:50}")
	private Integer batchSize;
	/** 线程池 */
    @Autowired
	private ThreadPoolTaskExecutor baseTaskThreadPool;

    @Resource(name="generateTimerTaskExecuteService")
    private TaskExcuteService generateTimerTaskExecuteService;

    @Resource(name="timerTaskConfigManager")
    private TimerTaskConfigManager timerTaskConfigManager;

	@Override
	public void execute() throws Exception {
		try {
            List<TimerTaskConfig> timerTaskConfigList = timerTaskConfigManager.findByBizTime(1,new Date());
			if(timerTaskConfigList != null && timerTaskConfigList.size() > 0){
				int listSize = timerTaskConfigList.size();
				int batchTimes = WorkerUtil.computeBatchTimes(listSize, batchSize);
				for (int i = 0; i < batchTimes; i++) {
					SimpleWorkerThread swt = new SimpleWorkerThread();
					int from = WorkerUtil.computeBatchFromIndex(listSize, batchSize, i);
					int to = WorkerUtil.computeBatchToIndex(listSize, batchSize, i);
					swt.setTaskList(timerTaskConfigList.subList(from,to));
					swt.setSimpleTaskService(generateTimerTaskExecuteService);
                    this.setSimpleWorkerThreadList(baseTaskThreadPool, generateTimerTaskExecuteService, timerTaskConfigList.subList(from,to));
				}
			}
		} catch (Exception e) {
			logger.error("子任务调度--生成定时任务异常:"+e);
            throw e;
		}
	}
}

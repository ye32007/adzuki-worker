package com.adzuki.worker.base.thread;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adzuki.worker.base.service.TaskExcuteService;

/**
 * 简单任务线程
 */
public class SimpleWorkerThread implements Runnable {
	
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleWorkerThread.class);
	
	/**简单任务服务接口*/
	private TaskExcuteService simpleTaskService;
	
	/**处理对象列表*/
	private List<? extends Object> taskList;
	
	@Override
	public void run() {
		long t1 = System.currentTimeMillis();
		if (logger.isDebugEnabled())
			logger.debug("当前线程:" + Thread.currentThread().getName());
		try {
			if(taskList!=null&&taskList.size()>0){
				for(Object task : taskList){
					simpleTaskService.process(task);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("任务处理异常：" + e);
		}
		long t5 = System.currentTimeMillis();
		logger.info("任务执行时间："+(t5-t1)+"ms");
	}

	public TaskExcuteService getSimpleTaskService() {
		return simpleTaskService;
	}

	public void setSimpleTaskService(TaskExcuteService simpleTaskService) {
		this.simpleTaskService = simpleTaskService;
	}

	public List<? extends Object> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<? extends Object> taskList) {
		this.taskList = taskList;
	}

}

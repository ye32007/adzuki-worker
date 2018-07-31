package com.adzuki.worker.base.executor;

/**
 * 调度执行接口
 */
public interface ScheduleExecutor {

	/**
	 * Description: quartz入口
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception;
}

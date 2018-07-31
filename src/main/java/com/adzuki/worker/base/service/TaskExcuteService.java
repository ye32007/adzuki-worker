package com.adzuki.worker.base.service;




/**
 * 简单任务接口
 */
public interface TaskExcuteService {

	/**
	 * 处理
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public boolean process(Object object) throws Exception;

}
package com.adzuki.worker.biz.dispatcher;

import com.adzuki.worker.domain.Task;

/**
 */
public interface TaskDispatcher {
	/**
	 *
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	boolean dispatch(Task obj) throws Exception;
}

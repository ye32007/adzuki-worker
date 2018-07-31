package com.adzuki.worker.base;

import java.util.Map;

public interface DataHandlerService<T> {
    /**
     * 处理子任务
     * @param task
     * @param paramMap
     * @return
     * @throws Exception
     */
	public boolean handler(T task, Map<String, Object> paramMap) throws Exception;
}

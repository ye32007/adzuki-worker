package com.adzuki.worker.biz.service;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.DataHandlerService;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.util.JSONUtil;

/**
 * DEMO 
 */
@Service("valuePoolValSubService")
public class ValuePoolValSubService implements DataHandlerService<Task> {

    private static final Logger logger = LoggerFactory.getLogger(ValuePoolValSubService.class);

	@Override
	public boolean handler(Task task, Map<String, Object> paramMap) throws Exception {
//		logger.error(" task : {} ,params : {} " , JSONUtil.toJson(task) , JSONUtil.toJson(paramMap));
		return true;
	}

    



}

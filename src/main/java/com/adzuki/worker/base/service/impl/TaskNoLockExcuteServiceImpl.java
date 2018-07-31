package com.adzuki.worker.base.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.service.TaskExcuteService;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.mapper.TaskMapper;

@Service("taskUnLockService")
public class TaskNoLockExcuteServiceImpl implements TaskExcuteService {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskNoLockExcuteServiceImpl.class);

    @Resource(name="taskMapper")
    protected TaskMapper taskMapper;
    
    @Override
    public boolean process(Object object) throws Exception {
    	Task task = (Task)object;
        return taskMapper.unLockTask(task.getId(), task.getRetryCount())>0?true:false;
    }
}

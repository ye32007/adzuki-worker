package com.adzuki.worker.base.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.adzuki.worker.base.service.GenerateTimerTaskService;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.mapper.TaskMapper;
import com.adzuki.worker.mapper.TimerTaskConfigMapper;

@Service("generateTimerTaskService")
public class GenerateTimerTaskServiceImpl implements GenerateTimerTaskService {

    private static final Log logger = LogFactory.getLog(GenerateTimerTaskServiceImpl.class);

    @Resource(name="taskMapper")
    protected TaskMapper taskMapper;

    @Resource(name="timerTaskConfigMapper")
    private TimerTaskConfigMapper timerTaskConfigMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public boolean insertSubTask(Task timerTask, TimerTaskConfig timerTaskConfig){
        taskMapper.insert(timerTask);//保存定时任务
        timerTaskConfigMapper.updateByPrimaryKeySelective(timerTaskConfig);//更新分发任务的下次执行时间
        return true;
    }
}

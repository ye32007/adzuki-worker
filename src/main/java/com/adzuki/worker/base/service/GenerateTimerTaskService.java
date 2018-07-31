package com.adzuki.worker.base.service;

import com.adzuki.worker.domain.Task;
import com.adzuki.worker.domain.TimerTaskConfig;

public interface GenerateTimerTaskService {

    public boolean insertSubTask(Task timerTask, TimerTaskConfig timerTaskConfig);
}

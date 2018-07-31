package com.adzuki.worker.base.executor.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.executor.AbstractScheduleExecutor;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.enums.TaskEnum;
import com.adzuki.worker.manager.TaskManager;
import com.adzuki.worker.mapper.TaskMapper;
import com.adzuki.worker.util.DateUtil;

/**
 * 任务调度--删除历史已完成的任务
 */
@Service("taskDeleteExecutor")
public class TaskDeleteScheduleExecutorImpl extends AbstractScheduleExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TaskDeleteScheduleExecutorImpl.class);
    
    @Resource(name="taskManager")
    private TaskManager taskManager;

    @Resource(name="taskMapper")
    private TaskMapper taskMapper;
    
    @Value("${schedule.delete.days:-30}")
    private Integer days;
    
    @Override
    public void execute() throws Exception {
        try {
            if(days >= 0){
                days = -30;
            }
            List<Task> taskList = taskManager.findFromDate(TaskEnum.TaskStatus.finish.status(),DateUtil.addDays(new Date(),days));
            if(taskList!=null&&taskList.size()>0){
                int count = taskList.size();
                Long first = taskList.get(0).getId();
                Long end = taskList.get(count-1).getId();
                int deleteCount;
                if(first<end){
                    deleteCount= taskMapper.deleteFinishedTask(first,end);
                }else{
                    deleteCount = taskMapper.deleteFinishedTask(end,first);
                }
                logger.info("计划删除任务条数:"+count+",删除已完成的任务条数:" + deleteCount);
            }
        } catch (Exception e) {
        	logger.error("子任务调度--删除已完成的任务:" + e);
            throw e;
        }
    }
}

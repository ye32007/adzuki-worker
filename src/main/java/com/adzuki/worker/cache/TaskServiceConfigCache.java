package com.adzuki.worker.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.executor.DiscardPolicyWithBaseScheduleLog;
import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.mapper.TaskServiceConfigMapper;

/**
 */
@Service("taskServiceConfigCache")
public class TaskServiceConfigCache implements Cache {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscardPolicyWithBaseScheduleLog.class);
	
    @Resource(name = "taskServiceConfigMapper")
    private TaskServiceConfigMapper taskServiceConfigMapper;
    
    public Map<Integer, TaskServiceConfig> taskServiceConfigMap = new LinkedHashMap<Integer, TaskServiceConfig>();
    public Map<Integer, TaskServiceConfig> validTaskServiceConfigMap = new LinkedHashMap<Integer, TaskServiceConfig>();
    public Map<Integer, TaskServiceConfig> taskServiceConfigParentMap = new LinkedHashMap<Integer, TaskServiceConfig>();
    public List<Integer> validTaskTypeList = new ArrayList<Integer>();
    
    @PostConstruct
    public void init() {
        //所有的任务业务配置数据
        List<TaskServiceConfig> taskServiceConfigList = taskServiceConfigMapper.selectAll();
        //所有的数据 子任务类型为key 数据对象为value
        Map<Integer, TaskServiceConfig> taskServiceConfigMapTemp = new LinkedHashMap<Integer, TaskServiceConfig>();
        //有效的数据 子任务类型为key 数据对象为value
        Map<Integer, TaskServiceConfig> validTaskServiceConfigMapTemp = new LinkedHashMap<Integer, TaskServiceConfig>();
        //所有的数据 父任务类型为key 数据对象为value
        Map<Integer, TaskServiceConfig> taskServiceConfigParentMapTemp = new LinkedHashMap<Integer, TaskServiceConfig>();
        //有效的数据子任务类型集合
        List<Integer> validTaskTypeListTemp = new ArrayList<Integer>();
        for(TaskServiceConfig taskServiceConfig:taskServiceConfigList){
        	taskServiceConfigMapTemp.put(taskServiceConfig.getTaskType(),taskServiceConfig);
            if(taskServiceConfig.getStatus() != null && taskServiceConfig.getStatus() == 1){//有效
            	validTaskServiceConfigMapTemp.put(taskServiceConfig.getTaskType(),taskServiceConfig);
                validTaskTypeListTemp.add(taskServiceConfig.getTaskType());
            }
            Integer taskParentType = taskServiceConfig.getTaskParentType();
            if(taskParentType != null){
            	taskServiceConfigParentMapTemp.put(taskParentType,taskServiceConfig);
            }
        }
        taskServiceConfigMap = taskServiceConfigMapTemp;
        taskServiceConfigParentMap = taskServiceConfigParentMapTemp;
        validTaskServiceConfigMap = validTaskServiceConfigMapTemp;
        validTaskTypeList = validTaskTypeListTemp;
    }

    @Override
    public void reload() {
        this.init();
    }

    @Bean(name="taskServiceConfigParentMap")
    public Map<Integer, TaskServiceConfig> getTaskServiceConfigParentMap() {
        return taskServiceConfigParentMap;
    }

    @Bean(name="validTaskServiceConfigMap")
    public Map<Integer, TaskServiceConfig> getValidTaskServiceConfigMap() {
        return validTaskServiceConfigMap;
    }


    @Bean(name="validTaskTypeList")
    public List<Integer> getValidTaskTypeList() {
        return validTaskTypeList;
    }


}

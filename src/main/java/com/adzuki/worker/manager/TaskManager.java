package com.adzuki.worker.manager;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.adzuki.worker.common.AbstractManager;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.mapper.TaskMapper;
import com.adzuki.worker.web.vo.PageParam;
import com.adzuki.worker.web.vo.Result;
import com.adzuki.worker.web.vo.TaskQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service("taskManager")
public class TaskManager extends AbstractManager<Task>{
	
	 private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    @Resource(name="taskMapper")
    protected TaskMapper taskMapper;

    public boolean finish(Task task) throws Exception {
        return taskMapper.finishTask(task.getId())>0?true:false;
    }

    public List<Task> findFromDate(Integer taskStatus, Date bizTime) {
        Condition condition = new Condition(Task.class);
    	condition.createCriteria().andEqualTo("taskStatus", taskStatus).andLessThan("bizTime", bizTime);
    	PageHelper.startPage(0, 100);
    	 return taskMapper.selectByCondition(condition);
    }

    public List<Task> selectLockedTask(Date lastUpdate, Integer retryCount) throws Exception {
        Condition condition = new Condition(Task.class);
    	condition.createCriteria().andEqualTo("taskStatus", 1).andLessThan("retryCount", retryCount).andLessThan("lastUpdate", lastUpdate);
    	PageHelper.startPage(0, 100);
        return taskMapper.selectByCondition(condition);
    }

    public boolean lock(Task task) throws Exception {
        return taskMapper.lockTask(task.getId())>0?true:false;
    }
    
    public boolean resetTaskById(Long taskId,Integer taskStatus){
    	return taskMapper.resetTaskById(taskId, taskStatus) >0 ? true : false;
    }

    public List<Task> findByTaskType(Integer taskType,Integer taskStatus,Date startTime) {
    	Condition condition = new Condition(Task.class);
    	condition.createCriteria().andEqualTo("taskStatus", taskStatus).andEqualTo("taskType", taskType).andLessThan("bizTime", startTime);
    	PageHelper.startPage(0, 100);
        return taskMapper.selectByCondition(condition);
    }
    
    public Result<PageInfo<Task>> queryPage(TaskQuery query, PageParam pageParam) {
        try {
            Condition condition = new Condition(Task.class);
            Criteria criteria  = condition.createCriteria();
            condition.orderBy("id").desc();
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            if(query.getTaskType() != null){
            	criteria.andEqualTo("taskType", query.getTaskType());
            }
			if(query.getTaskStatus() != null){
				criteria.andEqualTo("taskStatus", query.getTaskStatus()); 	
			}
			if(StringUtils.isNoneBlank(query.getUuid())){
				criteria.andEqualTo("uuid", query.getUuid()); 	
			}
			if(StringUtils.isNoneBlank(query.getBizId())){
				criteria.andEqualTo("bizId", query.getBizId());
			}
			if(query.getStartTime() != null){
				criteria.andGreaterThanOrEqualTo("bizTime", query.getStartTime()); 	
			}
			if(query.getEndTime() != null){
				criteria.andLessThanOrEqualTo("bizTime", query.getEndTime()); 	
			}
            List<Task> result = findByCondition(condition);
            PageInfo<Task> pageInfo = new PageInfo<>(result);
            return Result.createSuccessResult(pageInfo);
        }catch (Exception e) {
            logger.error("查找TASK异常", e);
            return Result.createFailResult("系统异常");
        }
    }
}

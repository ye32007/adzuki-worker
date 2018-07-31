package com.adzuki.worker.manager;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.adzuki.worker.common.AbstractManager;
import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.mapper.TimerTaskConfigMapper;
import com.adzuki.worker.web.vo.PageParam;
import com.adzuki.worker.web.vo.Result;
import com.adzuki.worker.web.vo.TimerTaskConfigQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service("timerTaskConfigManager")
public class TimerTaskConfigManager extends AbstractManager<TimerTaskConfig> {

    private static final Logger logger = LoggerFactory.getLogger(TimerTaskConfigManager.class);
    
    @Autowired
    private TimerTaskConfigMapper timerTaskConfigMapper;
	
	public Result<PageInfo<TimerTaskConfig>> queryPage(TimerTaskConfigQuery query, PageParam pageParam) {
       try {
           Condition condition = new Condition(TaskServiceConfig.class);
           Criteria criteria  = condition.createCriteria();
           condition.orderBy("id").desc();
           PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
           if (StringUtils.isNotEmpty(query.getTaskTimerDesc())) {
           	criteria.andLike("taskTimerDesc", "%" + query.getTaskTimerDesc() + "%");
           }
           if (query.getStatus() != null) {
           	criteria.andEqualTo("status", query.getStatus());
           }
           List<TimerTaskConfig> result = findByCondition(condition);
           PageInfo<TimerTaskConfig> pageInfo = new PageInfo<>(result);
           return Result.createSuccessResult(pageInfo);
       }catch (Exception e) {
           logger.error("查找TASK异常", e);
           return Result.createFailResult("系统异常");
       }
   }

    public List<TimerTaskConfig> findAll() {
        List<TimerTaskConfig> timerTaskConfigList = timerTaskConfigMapper.selectAll();
        return timerTaskConfigList;
    }

    public Integer findCount() {
    	Integer count = timerTaskConfigMapper.selectCount(TimerTaskConfig.builder().build());
        return count;
    }

    public List<TimerTaskConfig> queryBySelective(TimerTaskConfig timerTaskConfig) {
        List<TimerTaskConfig> timerTaskConfigList = timerTaskConfigMapper.select(timerTaskConfig);
        return timerTaskConfigList;
    }

    public Integer queryCountBySelective(TimerTaskConfig timerTaskConfig) {
        Integer count = timerTaskConfigMapper.selectCount(timerTaskConfig);
        return count;
    }

    public TimerTaskConfig queryByPrimaryKey(Integer id) {
    	return timerTaskConfigMapper.selectByPrimaryKey(id);
    }

    public int deleteByPrimaryKey(Integer id) {
    	return timerTaskConfigMapper.deleteByPrimaryKey(id);
    }

    public TimerTaskConfig queryByPrimaryKey(Long id) {
        TimerTaskConfig timerTaskConfig = timerTaskConfigMapper.selectByPrimaryKey(id);
        return timerTaskConfig;
    }

    public Boolean deleteByPrimaryKey(Long id) {
        int result = timerTaskConfigMapper.deleteByPrimaryKey(id);
        return result == 0 ? false : true;

    }

    public Boolean updateByPrimaryKeySelective(TimerTaskConfig timerTaskConfig) {
        int result = timerTaskConfigMapper.updateByPrimaryKeySelective(timerTaskConfig);
        return result == 0 ? false : true;
    }

    public Boolean deleteByUniqueIndextaskTimerType(Integer taskTimerType) {
        int result = timerTaskConfigMapper.deleteByUniqueIndextaskTimerType(taskTimerType);
        return result == 0 ? false : true;
    }

    public Boolean deleteByUniqueIndextaskTimerKey(String taskTimerKey) {
        int result = timerTaskConfigMapper.deleteByUniqueIndextaskTimerKey(taskTimerKey);
        return result == 0 ? false : true;
    }

    public List<TimerTaskConfig> findByBizTime(Integer status , Date bizTime) {
    	Condition condition = new Condition(TimerTaskConfig.class);
    	condition.createCriteria().andEqualTo("status", status).andLessThan("bizTime", bizTime);
        return timerTaskConfigMapper.selectByCondition(condition);
    }

}
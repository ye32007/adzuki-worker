package com.adzuki.worker.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.adzuki.worker.common.AbstractManager;
import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.web.vo.PageParam;
import com.adzuki.worker.web.vo.Result;
import com.adzuki.worker.web.vo.TaskServiceConfigQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


@Service("taskServiceConfigManager")
public class TaskServiceConfigManager extends AbstractManager<TaskServiceConfig> {

	 private static final Logger logger = LoggerFactory.getLogger(TaskServiceConfigManager.class);
	
	 public Result<PageInfo<TaskServiceConfig>> queryPage(TaskServiceConfigQuery query, PageParam pageParam) {
        try {
            Condition condition = new Condition(TaskServiceConfig.class);
            Criteria criteria  = condition.createCriteria();
            condition.orderBy("id").desc();
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            if (StringUtils.isNotEmpty(query.getTaskService())) {
            	criteria.andLike("taskService", "%" + query.getTaskService() + "%");
            }
            if (query.getStatus() != null) {
            	criteria.andEqualTo("status", query.getStatus());
            }
            List<TaskServiceConfig> result = findByCondition(condition);
            PageInfo<TaskServiceConfig> pageInfo = new PageInfo<>(result);
            return Result.createSuccessResult(pageInfo);
        }catch (Exception e) {
            logger.error("查找TASK异常", e);
            return Result.createFailResult("系统异常");
        }
    }
}

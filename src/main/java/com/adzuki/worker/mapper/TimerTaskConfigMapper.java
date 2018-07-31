package com.adzuki.worker.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import com.adzuki.worker.common.CoreMapper;
import com.adzuki.worker.domain.TimerTaskConfig;

public interface TimerTaskConfigMapper extends CoreMapper<TimerTaskConfig> {

	@Delete("DELETE FROM timer_task_config WHERE task_timer_type=#{taskTimerType} and  1=1")
	public int deleteByUniqueIndextaskTimerType(@Param("taskTimerType") Integer taskTimerType);
	
	@Delete("DELETE FROM timer_task_config WHERE task_timer_key=#{taskTimerKey} and   1=1")
	public int deleteByUniqueIndextaskTimerKey(@Param("taskTimerKey") String taskTimerKey);
	
//	@Select("FROM timer_task_config where status=#{status} and biz_time < #{bizTime}")
//	public List<TimerTaskConfig> findByBizTime(@Param("status") int status,@Param("bizTime") Date bizTime);

}
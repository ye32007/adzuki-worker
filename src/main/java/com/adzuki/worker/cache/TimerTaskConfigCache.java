package com.adzuki.worker.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.adzuki.worker.base.executor.DiscardPolicyWithBaseScheduleLog;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.mapper.TimerTaskConfigMapper;

/**
 */
@Service("timerTaskConfigCache")
public class TimerTaskConfigCache implements Cache {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscardPolicyWithBaseScheduleLog.class);
	
	@Resource(name = "timerTaskConfigMapper")
	private TimerTaskConfigMapper timerTaskConfigMapper;
	
	public Map<Integer, TimerTaskConfig> timerTaskConfigMap = new HashMap<Integer, TimerTaskConfig>();

	@PostConstruct
	public void init() {
		List<TimerTaskConfig> timerTaskConfigList = timerTaskConfigMapper.selectAll();
		Map<Integer, TimerTaskConfig> temp = new HashMap<Integer, TimerTaskConfig>();
		for (TimerTaskConfig timerTaskConfig : timerTaskConfigList) {
			temp.put(timerTaskConfig.getTaskTimerType(), timerTaskConfig);
		}
		timerTaskConfigMap = temp;
	}

	@Override
	public void reload() {
		this.init();
	}
}

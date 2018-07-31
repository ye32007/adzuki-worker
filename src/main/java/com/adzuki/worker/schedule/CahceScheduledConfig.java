package com.adzuki.worker.schedule;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adzuki.worker.cache.Cache;
import com.adzuki.worker.util.ApplicationContextUtils;

@Component
public class CahceScheduledConfig{
	
	private static final Log logger = LogFactory.getLog(CahceScheduledConfig.class);
	
	@Scheduled(cron = "${schedule.cache}")
	public void reload(){
		logger.info("cache reload...");
		Map<String, Cache> result = ApplicationContextUtils.getApplicationContext().getBeansOfType(Cache.class);
		if(!result.keySet().isEmpty()){
			result.forEach((k,v)->{
				v.reload();
			});
		}
	}
	
		
	
}

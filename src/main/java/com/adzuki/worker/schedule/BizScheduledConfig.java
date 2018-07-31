package com.adzuki.worker.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.adzuki.worker.biz.executor.BizScheduleExecutor;
import com.adzuki.worker.biz.executor.DiscardPolicyWithBizScheduleLog;


@Component
public class BizScheduledConfig {
	
	@Value("${schedule.biz.corePoolSize:10}")
	private Integer corePoolSize;
	@Value("${schedule.biz.maxPoolSize:100}")
	private Integer maxPoolSize;
	@Value("${schedule.biz.queueCapacity:500}")
	private Integer queueCapacity;
	@Value("${schedule.biz.keepAliveSeconds:5}")
	private Integer keepAliveSeconds;
	
	@Autowired
	private BizScheduleExecutor bizTaskExecutor;
	
	//=========================== 定义任务 =============================== //
	
	@Scheduled(fixedDelayString  = "${schedule.biz.fixedDelay:3000}" , initialDelay = 1000)
	public void biz() throws Exception{
		bizTaskExecutor.execute();
	}
	
	//=========================== 定义线程池 =============================== //

	@Bean(name  = "bizTaskThreadPool" , destroyMethod="shutdown")
	public ThreadPoolTaskExecutor taskExecutors(){
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);//<!-- 核心线程数  -->
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);//<!-- 最大线程数 -->
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);//<!-- 队列最大长度 -->
		threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);//<!-- 线程池维护线程所允许的空闲时间 -->
		threadPoolTaskExecutor.setRejectedExecutionHandler(policy());
		return threadPoolTaskExecutor;
	}
	
	@Bean(name = "bizRejectionPolicy")
	public DiscardPolicyWithBizScheduleLog policy(){
		DiscardPolicyWithBizScheduleLog policy = new DiscardPolicyWithBizScheduleLog();
		policy.setThreadName("bizTaskThreadPool");
		return policy;
	}
}

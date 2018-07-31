package com.adzuki.worker.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.adzuki.worker.base.executor.DiscardPolicyWithBaseScheduleLog;
import com.adzuki.worker.base.executor.ScheduleExecutor;


@Component
public class BaseScheduledConfig {
	
	@Value("${schedule.base.corePoolSize:3}")
	private Integer corePoolSize;
	@Value("${schedule.base.maxPoolSize:10}")
	private Integer maxPoolSize;
	@Value("${schedule.base.queueCapacity:100}")
	private Integer queueCapacity;
	@Value("${schedule.base.keepAliveSeconds:5}")
	private Integer keepAliveSeconds;
	
	@Autowired
	private ScheduleExecutor taskDeleteExecutor;
	@Autowired
	private ScheduleExecutor taskUnlockExecutor;
	@Autowired
	private ScheduleExecutor timerTaskExecutor;
	
	//=========================== 定义任务 =============================== //
	@Scheduled(fixedDelayString = "${schedule.timer.fixedDelay:15000}" , initialDelay = 1000)
	public void timer() throws Exception{
		timerTaskExecutor.execute();
	}
	
	@Scheduled(fixedDelayString = "${schedule.unlock.fixedDelay:60000}", initialDelay = 1000)
	public void unlock() throws Exception{
		taskUnlockExecutor.execute();
	}
	
	@Scheduled(fixedDelayString = "${schedule.unlock.fixedDelay:1800000}", initialDelay = 1000)
	public void delete() throws Exception{
		taskDeleteExecutor.execute();
	}
	
	//=========================== 定义线程池 =============================== //

	@Bean(name  = "baseTaskThreadPool" , destroyMethod="shutdown")
	public ThreadPoolTaskExecutor baseTaskExecutors(){
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);//<!-- 核心线程数  -->
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);//<!-- 最大线程数 -->
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);//<!-- 队列最大长度 -->
		threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);//<!-- 线程池维护线程所允许的空闲时间 -->
		threadPoolTaskExecutor.setRejectedExecutionHandler(policy());
		return threadPoolTaskExecutor;
	}
	
	@Bean(name  = "taskunLockThreadPool" , destroyMethod="shutdown")
	public ThreadPoolTaskExecutor unLockTaskExecutors(){
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);//<!-- 核心线程数  -->
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);//<!-- 最大线程数 -->
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);//<!-- 队列最大长度 -->
		threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);//<!-- 线程池维护线程所允许的空闲时间 -->
		threadPoolTaskExecutor.setRejectedExecutionHandler(unlockPolicy());
		return threadPoolTaskExecutor;
	}
	
	@Bean(name = "baseRejectionPolicy")
	public DiscardPolicyWithBaseScheduleLog policy(){
		DiscardPolicyWithBaseScheduleLog policy = new DiscardPolicyWithBaseScheduleLog();
		policy.setThreadName("baseTaskThreadPool");
		return policy;
	}
	
	@Bean(name = "unlockRejectionPolicy")
	public DiscardPolicyWithBaseScheduleLog unlockPolicy(){
		DiscardPolicyWithBaseScheduleLog policy = new DiscardPolicyWithBaseScheduleLog();
		policy.setThreadName("taskunLockThreadPool");
		return policy;
	}
}

package com.adzuki.worker.base.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.adzuki.worker.base.service.GenerateTimerTaskService;
import com.adzuki.worker.base.service.TaskExcuteService;
import com.adzuki.worker.cache.TimerTaskConfigCache;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.enums.TaskEnum;
import com.adzuki.worker.util.DateUtil;
import com.adzuki.worker.util.JSONUtil;

/**
 * 创建定时每月、每日或者每小时等类型执行的任务
 */

@Service("generateTimerTaskExecuteService")
public class GenerateTimerTaskExecuteServiceImpl implements TaskExcuteService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(GenerateTimerTaskExecuteServiceImpl.class);

    @Resource(name = "timerTaskConfigCache")
    TimerTaskConfigCache timerTaskConfigCache;

    @Autowired
    private GenerateTimerTaskService generateTimerTaskService;

    @Override
    public boolean process(Object object) throws Exception {
        boolean result =true;
        try{
            SimpleDateFormat yyyymmddhhmmssDateFormat = DateUtil.getDateFormat("yyyyMMddHHmmss");
            Assert.notNull(object);
            TimerTaskConfig timerTaskConfig = (TimerTaskConfig)object;
            Integer taskTimerType = timerTaskConfig.getTaskTimerType();
            TimerTaskConfig timerTaskConfigFromCache  = timerTaskConfigCache.timerTaskConfigMap.get(taskTimerType);
            if(timerTaskConfigFromCache == null || timerTaskConfigFromCache.getStatus()== 0){//如果不存在或者停止了，不执行后面的
                return false;
            }
            String dataType = timerTaskConfig.getDataType();
            Calendar now = Calendar.getInstance();
            Task timerTask = new Task();//定时任务
            if(dataType.equals("month")){//每月执行
            	if(timerTaskConfig.getMonth() == null || timerTaskConfig.getMonth().intValue() == 0) {
            		now.set(Calendar.MONTH,now.get(Calendar.MONTH)+1);
            	} else {
            		now.set(Calendar.MONTH,now.get(Calendar.MONTH)+timerTaskConfig.getMonth().intValue());
            	}
                now.set(Calendar.DAY_OF_MONTH,timerTaskConfig.getDay().intValue());
                now.set(Calendar.HOUR_OF_DAY,timerTaskConfig.getHour().intValue());
                now.set(Calendar.MINUTE,timerTaskConfig.getMinute().intValue());
                now.set(Calendar.SECOND,timerTaskConfig.getSecond().intValue());
                timerTask.setBizTime(now.getTime());
            }else if(dataType.equals("day")){//每日执行
            	if(timerTaskConfig.getDay() == null || timerTaskConfig.getDay().intValue() == 0) {
            		now.set(Calendar.DAY_OF_MONTH,now.get(Calendar.DAY_OF_MONTH)+1);
            	} else {
            		now.set(Calendar.DAY_OF_MONTH,now.get(Calendar.DAY_OF_MONTH)+timerTaskConfig.getDay().intValue());
            	}
                now.set(Calendar.HOUR_OF_DAY,timerTaskConfig.getHour().intValue());
                now.set(Calendar.MINUTE,timerTaskConfig.getMinute().intValue());
                now.set(Calendar.SECOND,timerTaskConfig.getSecond().intValue());
                timerTask.setBizTime(now.getTime());
            }else if(dataType.equals("hour")){//每小时执行
            	if(timerTaskConfig.getHour() == null || timerTaskConfig.getHour().intValue() == 0) {
            		now.set(Calendar.HOUR_OF_DAY,now.get(Calendar.HOUR_OF_DAY)+1);
            	} else {
            		now.set(Calendar.HOUR_OF_DAY,now.get(Calendar.HOUR_OF_DAY)+timerTaskConfig.getHour().intValue());
            	}
                now.set(Calendar.MINUTE,timerTaskConfig.getMinute().intValue());
                now.set(Calendar.SECOND,timerTaskConfig.getSecond().intValue());
                timerTask.setBizTime(now.getTime());
            }else if(dataType.equals("minute")){//每分钟执行
            	if(timerTaskConfig.getMinute() == null || timerTaskConfig.getMinute().intValue() == 0) {
            		now.set(Calendar.MINUTE,now.get(Calendar.MINUTE)+1);
            	} else {
            		now.set(Calendar.MINUTE,now.get(Calendar.MINUTE)+timerTaskConfig.getMinute().intValue());
            	}
                now.set(Calendar.SECOND,timerTaskConfig.getSecond().intValue());
                timerTask.setBizTime(now.getTime());
            }else{
                //TODO
            }
            timerTask.setTaskType(timerTaskConfig.getTaskTimerType());
            timerTask.setTaskStatus(TaskEnum.TaskStatus.init.status());
            StringBuffer timerTaskUUid = new StringBuffer();
            timerTaskUUid.append(timerTaskConfig.getTaskTimerType()).append("_");//taskType
            timerTaskUUid.append(yyyymmddhhmmssDateFormat.format(now.getTime()));//yyyymmddhhmmss
            timerTask.setUuid(timerTaskUUid.toString());
            timerTask.setRetryCount(0);
            Task dataTask = new Task();
            dataTask.setBizTime(now.getTime());
            timerTask.setTaskData(JSONUtil.toJson(dataTask));
            timerTaskConfig.setBizTime(timerTask.getBizTime());
            timerTask.setCreateTime(new Date());
            timerTask.setLastUpdate(new Date());
            generateTimerTaskService.insertSubTask(timerTask, timerTaskConfig);//写入子任务，更新分发任务的下次执行时间
            return result;
        }catch(Exception e){
        	logger.error("timer定时器生成任务异常", e);
            return false;
        }
    }




    public static void main(String[] args){

        SimpleDateFormat simpleDateFormat = DateUtil.getDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(simpleDateFormat.format(date));
        Calendar now = Calendar.getInstance();
        //now.set( now.get(Calendar.YEAR),1,28);
       // now.set(Calendar.YEAR,2014);
        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.MONTH,4);
        now.set(Calendar.SECOND,0);
       // now.set(Calendar.DAY_OF_MONTH,now.get(Calendar.DAY_OF_MONTH)+1);
        now.set(Calendar.DAY_OF_MONTH,13);
        System.out.println(simpleDateFormat.format(now.getTime()));
        long days =  (date.getTime()-now.getTime().getTime()) / (1000 * 60 * 60 * 24);
        System.out.println(days);
        now = Calendar.getInstance();
        System.out.println("年: " + now.get(Calendar.YEAR));
        System.out.println("月: " + (now.get(Calendar.MONTH) + 1) + "");
        System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));
        System.out.println("时: " + now.get(Calendar.HOUR_OF_DAY));
        System.out.println("分: " + now.get(Calendar.MINUTE));
        System.out.println("秒: " + now.get(Calendar.SECOND));

    }
}


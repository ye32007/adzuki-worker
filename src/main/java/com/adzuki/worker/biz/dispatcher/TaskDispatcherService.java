package com.adzuki.worker.biz.dispatcher;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.adzuki.worker.base.DataHandlerService;
import com.adzuki.worker.cache.TaskServiceConfigCache;
import com.adzuki.worker.domain.Task;
import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.util.ApplicationContextUtils;

/**
 * 将子任务分发给具体的service处理
 */
@Service("taskDispatcherService")
public class TaskDispatcherService implements TaskDispatcher{
	
    private static final Logger logger = LoggerFactory.getLogger(TaskDispatcherService.class);

    @Resource(name = "taskServiceConfigCache")
    TaskServiceConfigCache taskServiceConfigCache;
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
	public boolean dispatch(Task obj) throws Exception {
        boolean result =false;
        long startTime1 = System.currentTimeMillis();
        try{
            Assert.notNull(obj);
            logger.info("serviceDispatcher receive task,uuid="+obj.getUuid()+":"+obj.getTaskData());
            TaskServiceConfig taskServiceConfig = taskServiceConfigCache.taskServiceConfigMap.get(obj.getTaskType());
            if(taskServiceConfig == null || taskServiceConfig.getStatus().intValue() == 0){//不存在或者没有启动，继续锁定。可以去掉了
                return false;
            }
            //获取处理类
            DataHandlerService handlerService = (DataHandlerService) ApplicationContextUtils.getBean(taskServiceConfig.getTaskService());
            if(handlerService!=null){
                Assert.notNull(handlerService);
                result = handlerService.handler(obj,null);
            }else{
            	logger.error("任务id"+obj.getId()+",uuid"+obj.getUuid()+",tasktype"+obj.getTaskType());
            }
        }catch(Exception e){
        	logger.error(" TaskDispatcherService error,uuid="+obj.getUuid(), e);
            throw e;
        }finally {
            long endTime = System.currentTimeMillis() - startTime1;
            logger.info(obj.getUuid() + "分发子任务耗时" + (endTime / 1000));
        }
        return result;
    }



}

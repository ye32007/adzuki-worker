package com.adzuki.worker.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.adzuki.worker.common.CoreMapper;
import com.adzuki.worker.domain.Task;

public interface TaskMapper extends CoreMapper<Task> {

	@Update("UPDATE task set task_status=2,last_update=now() WHERE id = #{taskId}")
	public int finishTask(@Param("taskId") final Long taskId);

	@Update("UPDATE task set task_status=1,last_update=now() WHERE id = #{taskId} and task_status=0")
	public int lockTask(@Param("taskId") final Long taskId);

//	@Select("select * from task where task_status=1 and last_update < #{lastUpdate} and retry_count < #{retryCount} limit 0,100")
//	public List<Task> selectLockedTask(@Param("lastUpdate") final Date lastUpdate,@Param("retryCount") final Integer retryCount);

//	@Select("select * from task where task_type=#{taskType} and task_status=#{taskStatus} and biz_time < #{bizTime} limit 0,100")
//	public List<Task> findByTaskType(@Param("taskType") final Integer taskType,
//									@Param("taskStatus") final Integer taskStatus,
//									@Param("bizTime") final Date bizTime);

//	@Select("select * FROM task where task_status=#{taskStatus} and biz_time < #{bizTime} limit 0,200")
//	public List<Task> findFromDate(@Param("taskStatus") final Integer taskStatus, @Param("bizTime") final Date bizTime);

	@Update("update	task set task_status = 0,retry_count = retry_count+1,last_update =now(),biz_time=now() where id = #{taskId} and task_status=1 and retry_count = #{retryCount}")
	public int unLockTask(@Param("taskId") final Long taskId, @Param("retryCount") final Integer retryCount);

	@Update("update task set task_status=#{taskStatus},retry_count=0,result_status=0,last_update=NOW(),biz_time=now() where id=#{taskId} and biz_time < now()")
	public int resetTaskById(@Param("taskId") final Long taskId,@Param("taskStatus") Integer taskStatus);
	
	@Delete("delete t from task t where t.task_status = 2 AND t.create_time <= #{bizTime}")
	public int cleanTask(@Param("bizTime") final Date bizTime);
	
	 /**
     * 删除已经完成的任务
     * @return
     */
	@Delete("DELETE FROM task WHERE id >=#{start} and id<=#{end} and task_status=2")
    public int deleteFinishedTask(@Param("start")Long start,@Param("end") Long end);

}
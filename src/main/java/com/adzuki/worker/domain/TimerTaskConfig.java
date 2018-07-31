package com.adzuki.worker.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 *
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "timer_task_config")
public class TimerTaskConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 执行任务的类型
	 */
	private Integer taskTimerType;
	/**
	 * 定时任务描述
	 */
	private String taskTimerDesc;

	/**
	 * 数据类型 month表示每月执行一次，day表示每天执行一次，hour表示每小时执行一次,minute表示每分钟执行
	 */
	private String dataType;

	/**
     * 
     */
	private Integer year;

	/**
     * 
     */
	private Integer month;

	/**
     * 
     */
	private Integer day;

	/**
     * 
     */
	private Integer hour;

	/**
     * 
     */
	private Integer minute;

	/**
     * 
     */
	private Integer second;

	/**
     * 
     */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date bizTime;

	/**
	 * 任务状态。0:停止，1:启动
	 */
	private Integer status;

	/**
     * 
     */
	private Date createTime;

	/**
     * 
     */
	private Date lastUpdate;

}
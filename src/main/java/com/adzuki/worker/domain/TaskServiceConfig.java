package com.adzuki.worker.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Task_service_config")
public class TaskServiceConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 任务类型
	 */
	private Integer taskType;

	/**
	 * 任务类型描述
	 */
	private String taskTypeDesc;

	/**
	 * serviceId
	 */
	private String taskService;

	/**
	 * 父任务类型
	 */
	private Integer taskParentType;

	/**
     * 
     */
	private Integer status;

	/**
     * 
     */
	private Date createTime;

	/**
	 * 任务状态。0:停止，1:启动
	 */
	private Date lastUpdate;

}
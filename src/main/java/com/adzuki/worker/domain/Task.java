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

import com.alibaba.fastjson.annotation.JSONField;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * UUID
	 */
	private String uuid;

	/**
	 * bizId
	 */
	private String bizId;

	/**
	 * 上级任务id
	 */
	private Long parentId;

	/**
	 * 任务类型
	 */
	private Integer taskType;

	/**
	 * 任务数据
	 */
	private String taskData;

	/**
	 * 任务状态
	 */
	private Integer taskStatus;

	/**
	 * 运行次数
	 */
	private Integer retryCount;

	/**
	 * 创建时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date bizTime;

	/**
	 * 任务结果,0表示任务初始，1表示任务成功，2表示任务失败
	 */
	private Integer resultStatus;

	/**
	 * 最后修改时间
	 */
	private Date lastUpdate;

	/**
	 * 创建时间
	 */
	private Date createTime;

}
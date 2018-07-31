package com.adzuki.worker.web.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TaskQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer taskType;
	private Integer taskStatus;
	private String bizId;
	private String uuid;
	private Date startTime;
	private Date endTime;

}

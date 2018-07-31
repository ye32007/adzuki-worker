package com.adzuki.worker.web.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class TimerTaskConfigQuery implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String taskTimerDesc;
	private Integer status;


}

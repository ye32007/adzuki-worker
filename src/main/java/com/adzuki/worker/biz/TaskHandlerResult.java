package com.adzuki.worker.biz;

import lombok.Data;

@Data
public class TaskHandlerResult {

	String taskuuid;
	Boolean result;
	String resultmsg;

}

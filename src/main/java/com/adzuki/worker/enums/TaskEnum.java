package com.adzuki.worker.enums;

/**
 */
public class TaskEnum {

	public enum TaskStatus {
		init(0, "子任务初始化"), lock(1, "子任务锁定"), finish(2, "子任务完成");
		private final Integer status;
		private final String desc;

		private TaskStatus(Integer status, String desc) {
			this.status = status;
			this.desc = desc;
		}

		public Integer status() {
			return status;
		}

		public String desc() {
			return desc;
		}
	}

	public enum TaskResultStatus {
		init(0, "任务结果初始化"), success(1, "任务结果成功"), fail(2, "任务结果失败");
		private final Integer status;
		private final String desc;

		private TaskResultStatus(Integer status, String desc) {
			this.status = status;
			this.desc = desc;
		}

		public Integer status() {
			return status;
		}

		public String desc() {
			return desc;
		}
	}

	/**
	 * 已使用金额重置的任务类型及任务key 、描述
	 */
	public enum USAGEAMOUNTUPDATE_TASKTYPE {
		YEAR(213, "year", "已使用年度金额"), SEASON(214, "season", "已使用季度金额"), MONTH(
				215, "month", "已使用月金额"), WEEK(216, "week", "已使用周金额");
		private final Integer taskType;
		private final String taskName;
		private final String desc;

		private USAGEAMOUNTUPDATE_TASKTYPE(Integer taskType, String taskName,
				String desc) {
			this.taskType = taskType;
			this.taskName = taskName;
			this.desc = desc;
		}

		public Integer taskType() {
			return taskType;
		}

		public String taskName() {
			return taskName;
		}

		public String desc() {
			return desc;
		}

	}
}

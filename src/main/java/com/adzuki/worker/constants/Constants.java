package com.adzuki.worker.constants;

public class Constants {

    /********************** 任务执行状态 ******************/

    /**
     * 任务未完成
     */
    public static final int TASK_STATUS_UNFINISHED = 0;

    /**
     * 任务锁定
     */
    public static final int TASK_STATUS_LOCK = 1;

    /**
     * 任务完成
     */
    public static final int TASK_STATUS_SUCCESS = 2;

    /**
     * 任务失败
     */
    public static final int TASK_STATUS_FAILED = 3;

    /**
     * 任务成功 订单取消
     */
    public static final int TASK_STATUS_SUCCESS_ORDER_CANCLE = 4;

    /**
     * 任务失败重试后成功
     */
    public static final int TASK_STATUS_FAILED_RESET_SUCCESS = 5;

    /**
     * 订单状态已被更新 任务应该为成功
     */
    public static final int TASK_STATUS_SUCCESS_ORDER_UPDATED = 6;


    /*************** 任务处理默认配置 ************************/
    /**
     * 默认失败重试次数
     */
    public static final int TASK_CONFIG_DEFAULT_RETRYCOUNT = 100;

    /**
     * 开启两次解锁时第二次解锁失败重试次数开始
     */
    public static final int TASK_CONFIG_RETRYCOUNT_2_START = 6;

    /**
     * 开启两次解锁时第二次解锁失败重试次数结束
     */
    public static final int TASK_CONFIG_RETRYCOUNT_2_END = 12;

    /**
     * 默认清除多少小时前的成功完成的任务数据(小时)
     */
    public static final int TASK_CONFIG_CLEAR_DEFAULT_HOURAGO = 24;
    /**
     * 默认任务锁定最大时长（秒）
     */
    public static final int TASK_CONFIG_TASKLOCK_DEFAULT_MAXSECONDS = 5 * 60;
    /**
     * 默认任务累积数量限制
     */
    public static final int TASK_CONFIG_ACCUMULATE_DEFAULT_ALLOWNUMBER = 100;
    /**
     * 默认任务累积数量限制
     */
    public static final int TASK_CONFIG_LOCK_DEFAULT_ALLOWSECONDS = 10 * 60;
    /**
     * 默认任务累积数量限制
     */
    public static final int TASK_CONFIG_UNPROCCESS_DEFAULT_ALLOWSECONDS = 10 * 60;
    /**
     * 默认每次批量处理的大小
     */
    public static final int TASK_BATCH_DEFAULT_SIZE = 10;

    


}

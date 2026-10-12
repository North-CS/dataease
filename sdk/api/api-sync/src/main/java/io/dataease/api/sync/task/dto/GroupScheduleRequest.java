package io.dataease.api.sync.task.dto;

import lombok.Data;

/**
 * 任务组调度配置
 */
@Data
public class GroupScheduleRequest {

    /**
     * 任务组节点ID
     */
    private String id;

    /**
     * 调度类型:NONE不调度/CRON定时/FIX_RATE固定频率
     */
    private String schedulerType;

    /**
     * 调度配置:cron表达式 或 固定间隔(如5m/1h)
     */
    private String schedulerConf;

    /**
     * 失败策略:CONTINUE 某任务失败后继续执行后续任务/ABORT 立即中止整组
     */
    private String failPolicy;

    /**
     * 并发策略:SKIP 上一轮未结束时跳过本次触发/QUEUE 排队等待上一轮结束
     */
    private String overlapPolicy;
}

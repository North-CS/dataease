package io.dataease.api.sync.task.vo;

import lombok.Data;

/**
 * 任务组执行日志 VO
 */
@Data
public class GroupLogVO {

    private String id;

    /**
     * 任务组ID
     */
    private String groupId;

    /**
     * 任务组名称（跨组查询时联表取得）
     */
    private String groupName;

    /**
     * 触发方式: MANUAL/CRON
     */
    private String triggerType;

    /**
     * 执行状态: RUNNING/SUCCESS/PARTIAL_FAIL/FAIL
     */
    private String status;

    private Long executorStartTime;
    private Long executorEndTime;

    private String executorMsg;

    /**
     * 成员任务执行摘要 JSON
     */
    private String taskSummary;
}

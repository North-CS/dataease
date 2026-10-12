package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务组终止执行请求
 */
@Data
public class GroupTerminateRequest implements Serializable {

    /**
     * 组执行日志ID
     */
    private String groupLogId;

    /**
     * 终止范围：CURRENT_ONE只终止当前任务 / ABORT_ALL终止当前任务并中止整组
     */
    private String scope;
}

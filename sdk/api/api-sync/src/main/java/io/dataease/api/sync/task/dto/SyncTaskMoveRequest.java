package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.util.List;

/**
 * 任务移动到目录请求
 */
@Data
public class SyncTaskMoveRequest {

    /**
     * 任务ID列表
     */
    private List<String> taskIds;

    /**
     * 目标节点ID（须为folder类型）
     */
    private String targetPid;
}

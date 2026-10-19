package io.dataease.api.sync.task.dto;

import lombok.Data;

/**
 * 树节点拖拽请求：统一处理移动/排序/任务移动目录
 */
@Data
public class SyncTreeDragRequest {

    /**
     * 被拖拽的节点ID
     */
    private String nodeId;

    /**
     * 拖拽后的目标父节点ID，拖到根为空
     */
    private String targetPid;

    /**
     * 目标位置在同级中的下标
     */
    private Integer targetIndex;
}

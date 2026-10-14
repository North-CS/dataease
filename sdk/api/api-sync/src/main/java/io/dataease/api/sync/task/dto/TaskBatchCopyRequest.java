package io.dataease.api.sync.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量复制任务请求：复制为待编辑任务（不执行，编辑保存后转正）
 */
@Data
public class TaskBatchCopyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(hidden = true)
    private List<String> taskIds;

    /**
     * 目标节点（目录或任务组）
     */
    @Schema(hidden = true)
    private String targetNodeId;
}

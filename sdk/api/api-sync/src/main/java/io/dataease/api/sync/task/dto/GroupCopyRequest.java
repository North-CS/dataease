package io.dataease.api.sync.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务组复制请求：在目标目录下新建任务组，组内成员复制为待编辑任务
 */
@Data
public class GroupCopyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 源任务组节点 ID
     */
    @Schema(hidden = true)
    private String groupId;

    /**
     * 目标目录节点 ID（任务组是最末层级，目标只能是目录）
     */
    @Schema(hidden = true)
    private String targetNodeId;
}

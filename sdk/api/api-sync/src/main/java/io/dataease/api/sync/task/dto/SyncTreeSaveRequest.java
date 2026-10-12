package io.dataease.api.sync.task.dto;

import lombok.Data;

/**
 * 树节点保存请求：新增或重命名
 */
@Data
public class SyncTreeSaveRequest {

    /**
     * 编辑时传，新增为空
     */
    private String id;

    private String name;

    /**
     * 父节点ID，根为空
     */
    private String pid;

    /**
     * 节点类型:folder目录/group任务组
     */
    private String nodeType;
}

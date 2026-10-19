package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 组内任务排序请求
 */
@Data
public class GroupTaskOrderRequest implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 方向：up上移 / down下移
     */
    private String direction;
}

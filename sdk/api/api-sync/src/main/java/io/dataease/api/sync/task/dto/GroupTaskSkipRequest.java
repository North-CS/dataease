package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 组内任务忽略执行开关请求
 */
@Data
public class GroupTaskSkipRequest implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 是否忽略执行
     */
    private Boolean skip;
}

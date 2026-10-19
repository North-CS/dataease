package io.dataease.api.sync.task.dto;

import lombok.Data;

/**
 * 同步任务结束后的通知。渠道和收件人在任务 extParameter 里，不进 SeaTunnel 参数。
 */
@Data
public class SyncTaskNotice {

    private String taskId;

    private String taskName;

    private boolean success;

    private String message;

    private String extParameter;
}

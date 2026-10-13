package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.util.List;

/**
 * 同步任务 extParameter.notify。放在 sdk 里，避免 xpack Allatori 把 JSON 字段名加密后取不到 uidList。
 */
@Data
public class SyncTaskNotifyConfig {

    private List<Integer> channels;

    private SyncTaskRecipients onSuccess;

    private SyncTaskRecipients onFail;
}

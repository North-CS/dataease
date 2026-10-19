package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.util.List;

/**
 * 同步通知一侧的接收人。uidList 是用户，ridList 是角色，和 reci-select 的拆分一致。
 */
@Data
public class SyncTaskRecipients {

    private List<Object> uidList;

    private List<Object> ridList;
}

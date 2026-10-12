package io.dataease.api.sync.task.dto;

import io.dataease.model.KeywordRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 任务组执行日志查询条件（跨组）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupLogGridRequest extends KeywordRequest implements Serializable {

    /**
     * 任务组ID，为空表示查询全部任务组
     */
    private String groupId;

    /**
     * 执行状态: RUNNING/SUCCESS/PARTIAL_FAIL/FAIL
     */
    private List<String> status;

    /**
     * 执行开始时间范围（毫秒时间戳）
     */
    private Long startTime;

    private Long endTime;
}

package io.dataease.api.sync.task.vo;

import lombok.Data;

import java.util.List;

/**
 * 同步任务树节点（目录/任务组）
 */
@Data
public class SyncTreeNodeVO {

    private String id;

    private String name;

    private String pid;

    /**
     * 节点类型:folder目录/group任务组
     */
    private String nodeType;

    /**
     * 同级排序
     */
    private Integer sort;

    /**
     * 节点下直属任务数
     */
    private Long taskCount;

    /**
     * 调度类型(NONE/CRON/FIX_RATE)，仅group有效
     */
    private String schedulerType;

    /**
     * 调度配置(cron或5m/1h)，仅group有效
     */
    private String schedulerConf;

    /**
     * 任务组失败策略
     */
    private String failPolicy;

    /**
     * 任务组并发策略
     */
    private String overlapPolicy;

    private List<SyncTreeNodeVO> children;
}

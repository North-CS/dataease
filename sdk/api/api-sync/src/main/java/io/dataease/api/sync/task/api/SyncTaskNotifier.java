package io.dataease.api.sync.task.api;

import io.dataease.api.sync.task.dto.SyncTaskNotice;

/**
 * 同步模块只依赖这个接口。邮件和京 ME 的实现在 xpack-base，走 CommunicateFactory。
 */
public interface SyncTaskNotifier {

    void notify(SyncTaskNotice notice);
}

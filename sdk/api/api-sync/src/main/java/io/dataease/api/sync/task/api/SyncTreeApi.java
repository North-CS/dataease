package io.dataease.api.sync.task.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.sync.task.dto.GroupScheduleRequest;
import io.dataease.api.sync.task.dto.GroupLogGridRequest;
import io.dataease.api.sync.task.dto.GroupTaskOrderRequest;
import io.dataease.api.sync.task.dto.GroupTaskSkipRequest;
import io.dataease.api.sync.task.dto.GroupTerminateRequest;
import io.dataease.api.sync.task.dto.SyncTaskMoveRequest;
import io.dataease.api.sync.task.dto.SyncTreeDragRequest;
import io.dataease.api.sync.task.dto.SyncTreeSaveRequest;
import io.dataease.api.sync.task.vo.GroupLogVO;
import io.dataease.api.sync.task.vo.SyncTreeNodeVO;
import io.dataease.auth.DeApiPath;
import io.dataease.exception.DEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.TASK;

/**
 * 同步任务树（目录/任务组）
 */
@Tag(name = "同步管理:任务树")
@ApiSupport(order = 889, author = "fit2cloud-someone")
@DeApiPath(value = "/sync/tree", rt = TASK)
public interface SyncTreeApi {

    @Operation(hidden = true)
    @GetMapping("/tree")
    List<SyncTreeNodeVO> tree() throws DEException;

    @Operation(hidden = true)
    @PostMapping("/save")
    void save(@RequestBody SyncTreeSaveRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/drag")
    void drag(@RequestBody SyncTreeDragRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/remove/{id}")
    void remove(@PathVariable("id") String id) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/move-task")
    void moveTask(@RequestBody SyncTaskMoveRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/executeGroup/{id}")
    String executeGroup(@PathVariable("id") String id) throws DEException;

    @Operation(hidden = true)
    @GetMapping("/groupLog/{groupId}/{goPage}/{pageSize}")
    IPage<GroupLogVO> groupLog(@PathVariable("groupId") String groupId,
                               @PathVariable("goPage") int goPage,
                               @PathVariable("pageSize") int pageSize) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/groupTask/order")
    void orderGroupTask(@RequestBody GroupTaskOrderRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/groupLog/pager/{goPage}/{pageSize}")
    IPage<GroupLogVO> groupLogPager(@PathVariable("goPage") int goPage,
                                    @PathVariable("pageSize") int pageSize,
                                    @RequestBody GroupLogGridRequest request) throws DEException;

    @Operation(hidden = true)
    @GetMapping("/groupLog/taskLog/{groupLogId}/{taskId}")
    String groupTaskLogId(@PathVariable("groupLogId") String groupLogId,
                          @PathVariable("taskId") String taskId) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/groupSchedule")
    void saveGroupSchedule(@RequestBody GroupScheduleRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/groupLog/terminate")
    void terminateGroup(@RequestBody GroupTerminateRequest request) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/groupTask/skip")
    void setGroupTaskSkip(@RequestBody GroupTaskSkipRequest request) throws DEException;
}

package io.dataease.api.sync.task.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.sync.task.dto.FieldMappingExcelRequest;
import io.dataease.api.sync.task.dto.GroupCopyRequest;
import io.dataease.api.sync.task.dto.TaskBatchCopyRequest;
import io.dataease.api.sync.task.dto.TaskGridRequest;
import io.dataease.api.sync.task.dto.TaskInfoDTO;
import io.dataease.api.sync.task.vo.FieldImportVO;
import io.dataease.api.sync.task.vo.TaskInfoVO;
import io.dataease.auth.DeApiPath;
import io.dataease.exception.DEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.TASK;

/**
 * @author fit2cloud
 * @date 2023/11/20 10:14
 **/
@Tag(name = "同步管理:任务管理")
@ApiSupport(order = 888, author = "fit2cloud-someone")
@DeApiPath(value = "/sync/task", rt = TASK)
public interface TaskApi {

    @Operation(hidden = true)
    @PostMapping("/pager/{goPage}/{pageSize}")
    IPage<TaskInfoVO> pager(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody TaskGridRequest request);

    @Operation(hidden = true)
    @PostMapping("/add")
    void add(@RequestBody TaskInfoDTO jobInfo) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/update")
    void update(@RequestBody TaskInfoDTO jobInfo) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/remove/{id}")
    void remove(@PathVariable(value = "id") String id) throws DEException;

    @Operation(hidden = true)
    @GetMapping("start/{id}")
    void startJob(@PathVariable(value = "id") String id) throws DEException;

    @Operation(hidden = true)
    @GetMapping("stop/{id}")
    void stopJob(@PathVariable(value = "id") String id) throws DEException;

    @Operation(hidden = true)
    @GetMapping("/get/{id}")
    TaskInfoVO getOneById(@PathVariable(value = "id") String id) throws DEException;

    @Operation(summary = "执行一次任务")
    @GetMapping("/execute/{id}")
    void execute(@PathVariable(value = "id") String id) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/batch/del")
    void batchDelete(@RequestBody List<String> ids) throws DEException;

    @Operation(hidden = true)
    @PostMapping("/batch/copy")
    void batchCopy(@RequestBody TaskBatchCopyRequest request) throws DEException;

    /**
     * 任务组复制：在目标目录下新建任务组，组内成员复制为待编辑任务
     *
     * @return 新任务组节点 ID
     */
    @Operation(hidden = true)
    @PostMapping("/group/copy")
    String groupCopy(@RequestBody GroupCopyRequest request) throws DEException;

    @GetMapping("/query2Root/{id}")
    String query2Root(@PathVariable("id") Long id);

    @GetMapping("/getLogResourceId/{id}")
    Long getLogResourceId(@PathVariable("id") String id);

    /**
     * 导出字段映射 Excel（以源表字段为基准生成模板，已映射字段带出当前配置）
     */
    @Operation(hidden = true)
    @PostMapping("/field/export")
    void exportFieldTemplate(@RequestBody FieldMappingExcelRequest request) throws DEException;

    /**
     * 解析字段映射 Excel，仅校验并返回预览结果，不落库
     */
    @Operation(hidden = true)
    @PostMapping("/field/import")
    FieldImportVO importField(@RequestParam("file") MultipartFile file,
                              @RequestParam("mode") String mode,
                              @RequestParam("context") String context) throws DEException;

}

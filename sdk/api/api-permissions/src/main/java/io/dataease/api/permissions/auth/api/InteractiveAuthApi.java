package io.dataease.api.permissions.auth.api;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.auth.dto.*;
import io.dataease.api.permissions.auth.vo.PermissionValVO;
import io.dataease.api.permissions.auth.vo.ResourceNodeVO;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import io.dataease.model.ExportTaskDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "内部资源交互")
@ApiSupport(order = 998)
@Hidden
public interface InteractiveAuthApi {

    @Operation(summary = "查询菜单ID")
    @ApiOperationSupport(order = 1)
    @GetMapping("/menuIds")
    List<Long> menuIds();


    @Operation(summary = "查询资源树")
    @ApiOperationSupport(order = 2)
    @PostMapping("/resource")
    List<BusiNodeVO> resource(@RequestBody BusiNodeRequest request);

    /**
     * 下面3个接口为内部调用接口不对外开放
     *
     * @param creator
     */

    @Operation(summary = "同步保存资源")
    @ApiOperationSupport(order = 3)
    @PostMapping("/resource/create")
    void saveResource(@RequestBody BusiResourceCreator creator);

    @Operation(summary = "同步更新资源")
    @ApiOperationSupport(order = 4)
    @PostMapping("/resource/edit")
    void editResource(@RequestBody BusiResourceEditor editor);

    @Operation(summary = "同步删除资源")
    @ApiOperationSupport(order = 5)
    @GetMapping("/resource/del/{id}")
    void delResource(@PathVariable("id") Long id);

    @Operation(summary = "删除检测")
    @ApiOperationSupport(order = 6)
    @GetMapping("/resource/checkDel/{id}")
    boolean checkDel(@PathVariable("id") Long id);

    @Operation(summary = "移动资源")
    @ApiOperationSupport(order = 7)
    @PostMapping("/moveResource")
    void moveResource(@RequestBody BusiResourceMover mover);

    @Operation(summary = "权限校验")
    @ApiOperationSupport(order = 8)
    @PostMapping("/checkAuth")
    void checkAuth(@RequestBody BusiPerCheckDTO checkDTO);

    @Operation(summary = "根资源权限校验（虚拟根节点不在 per_busi_resource 中，须按资源类型直查授权表）")
    @ApiOperationSupport(order = 8)
    @PostMapping("/checkRootAuth")
    void checkRootAuth(@RequestParam("resourceType") Integer resourceType, @RequestParam("weight") Integer weight);

    @Operation(summary = "权限查询")
    @ApiOperationSupport(order = 9)
    @PostMapping("/queryAuth/{id}")
    PermissionValVO queryAuth(@PathVariable("id") Long id);

    @GetMapping("/query2Root/{id}/{flag}/{logOT}")
    List<ResourceNodeVO> query2Root(@PathVariable("id") Long id, @PathVariable("flag") Integer flag, Integer logOT);

    @GetMapping("/checkEmpty")
    boolean checkEmpty();

    @GetMapping("/OrgNameForResource")
    String OrgNameForResource(ExportTaskDTO exportTaskDTO);

    void editResourceExtraFlag(BusiResourceEditor editor);

    @PostMapping("/batchAuthorize")
    void batchAuthorize(@RequestBody BusiBatchAuthorizeRequest request);

    @Hidden
    @PostMapping("/revert")
    void revert();
}

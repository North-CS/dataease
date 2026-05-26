package io.dataease.api.cloudstorage;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.system.vo.SettingItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "系统设置:云存储")
@ApiSupport(order = 797)
public interface CloudStorageApi {

    @Operation(summary = "查询云存储设置")
    @GetMapping("/setting/query")
    List<SettingItemVO> queryCloudStorageSetting();

    @Operation(summary = "保存云存储设置")
    @PostMapping("/setting/save")
    void saveCloudStorageSetting(@RequestBody List<SettingItemVO> settingItemVOS);

    @Operation(summary = "校验云存储连接")
    @PostMapping("/setting/validate")
    void validate(@RequestBody List<SettingItemVO> settingItemVOS);
}

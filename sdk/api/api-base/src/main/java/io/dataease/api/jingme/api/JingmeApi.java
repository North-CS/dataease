package io.dataease.api.jingme.api;

import io.dataease.api.jingme.dto.JingmeEnableEditor;
import io.dataease.api.jingme.dto.JingmeSettingCreator;
import io.dataease.api.jingme.vo.JingmeInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "京ME设置")
public interface JingmeApi {

    @Operation(summary = "查询京ME信息")
    @GetMapping("/info")
    JingmeInfoVO info();

    @Operation(summary = "保存")
    @PostMapping("/create")
    void save(@RequestBody JingmeSettingCreator creator);

    @Operation(summary = "切换开启状态")
    @PostMapping("/switchEnable")
    void switchEnable(@RequestBody JingmeEnableEditor editor);

    @Operation(summary = "验证可用性")
    @PostMapping("/validate")
    void validate(@RequestBody JingmeSettingCreator creator);
}

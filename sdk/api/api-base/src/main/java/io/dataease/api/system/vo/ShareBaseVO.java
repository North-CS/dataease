package io.dataease.api.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "分享设置参数项")
@Data
public class ShareBaseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8418758128428770485L;
    @Schema(description = "禁用分享")
    private boolean disable;

    @Schema(description = "有效期密码必填")
    private boolean peRequire;
}

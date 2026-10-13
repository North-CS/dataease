package io.dataease.api.jingme.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class JingmeInfoVO implements Serializable {
    @Schema(description = "服务地址")
    private String baseUrl;
    @Schema(description = "appKey")
    private String appKey;
    @Schema(description = "appSecret")
    private String appSecret;
    @Schema(description = "openTeamId")
    private String openTeamId;
    @Schema(description = "robotId")
    private String robotId;
    @Schema(description = "tenantId")
    private String tenantId;
    @Schema(description = "是否开启")
    private Boolean enable = false;
    @Schema(description = "是否可用")
    private Boolean valid = false;
}

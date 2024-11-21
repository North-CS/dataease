package io.dataease.api.permissions.user.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CurIpVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3025566841330382707L;

    private String account;

    private String name;

    private String ip;
}

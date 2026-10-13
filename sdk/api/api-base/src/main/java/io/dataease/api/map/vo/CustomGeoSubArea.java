package io.dataease.api.map.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomGeoSubArea implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String scope;
    private String geoAreaId;
}

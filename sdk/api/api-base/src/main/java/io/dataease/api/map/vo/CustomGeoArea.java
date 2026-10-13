package io.dataease.api.map.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomGeoArea implements Serializable {
    private String id;
    private String name;
}

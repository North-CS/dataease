package io.dataease.extensions.datafilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Describes one logical DataFilling field type and its configurable DDL parameters.
 * Null bounds mean that the generic contract does not impose a provider-specific limit.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataFillingTypeCapability implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ExtTableField.BaseType logicalType;
    private String physicalType;
    private Integer defaultSize;
    private Integer defaultAccuracy;
    private Integer minSize;
    private Integer maxSize;
    private Integer minAccuracy;
    private Integer maxAccuracy;
    private boolean sizeConfigurable;
    private boolean accuracyConfigurable;

    public int effectiveSize(Integer configuredSize) {
        return configuredSize == null ? defaultSize : configuredSize;
    }

    public int effectiveAccuracy(Integer configuredAccuracy) {
        return configuredAccuracy == null ? defaultAccuracy : configuredAccuracy;
    }

    /** Formats the type only; provider validation remains the authority for invalid values. */
    public String preview(Integer configuredSize, Integer configuredAccuracy) {
        return switch (logicalType) {
            case nvarchar -> physicalType + "(" + effectiveSize(configuredSize) + ")";
            case decimal -> physicalType + "(" + effectiveSize(configuredSize) + ","
                    + effectiveAccuracy(configuredAccuracy) + ")";
            case number, text, datetime -> physicalType;
        };
    }
}

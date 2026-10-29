package io.dataease.extensions.datafilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Provider-neutral DataFilling DDL capability exposed to callers and plugins. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataFillingProviderCapability implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean chineseIdentifiers;
    private Integer maxRowBytes;
    private Integer maxComponents;
    @Builder.Default
    private List<DataFillingTypeCapability> types = new ArrayList<>();

    public DataFillingTypeCapability type(ExtTableField.BaseType logicalType) {
        if (logicalType == null || types == null) return null;
        return types.stream().filter(type -> type.getLogicalType() == logicalType).findFirst().orElse(null);
    }

    /**
     * Safe defaults for existing plugins. A plugin may override capability(), preview(), or
     * validation when its actual DDL differs; no new abstract method is introduced.
     */
    public static DataFillingProviderCapability generic() {
        List<DataFillingTypeCapability> types = new ArrayList<>();
        types.add(DataFillingTypeCapability.builder()
                .logicalType(ExtTableField.BaseType.nvarchar).physicalType("VARCHAR")
                .defaultSize(256).minSize(1).sizeConfigurable(true).build());
        types.add(DataFillingTypeCapability.builder()
                .logicalType(ExtTableField.BaseType.decimal).physicalType("DECIMAL")
                .defaultSize(20).defaultAccuracy(8).minSize(1).minAccuracy(0)
                .sizeConfigurable(true).accuracyConfigurable(true).build());
        types.add(DataFillingTypeCapability.builder()
                .logicalType(ExtTableField.BaseType.number).physicalType("BIGINT").build());
        types.add(DataFillingTypeCapability.builder()
                .logicalType(ExtTableField.BaseType.text).physicalType("LONGTEXT").build());
        types.add(DataFillingTypeCapability.builder()
                .logicalType(ExtTableField.BaseType.datetime).physicalType("DATETIME").build());
        return DataFillingProviderCapability.builder()
                .chineseIdentifiers(true).types(types).build();
    }
}

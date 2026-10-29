package io.dataease.extensions.datafilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Provider capabilities together with validated physical previews for a form definition. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataFillingFormCapability implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String datasourceType;
    private DataFillingProviderCapability providerCapability;
    @Builder.Default
    private List<FieldPreview> fieldPreviews = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldPreview implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String id;
        private String columnName;
        private ExtTableField.BaseType logicalType;
        private String physicalType;
        private Integer size;
        private Integer accuracy;
    }
}

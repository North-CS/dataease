package io.dataease.api.xpack.dataFilling.dto;

import io.dataease.extensions.datafilling.dto.ExtTableField;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/** Overrides are revalidated against every cell, never only the preview. */
@Data
public class ExcelParseOptions {
    private List<ColumnOverride> columns = new ArrayList<>();
    @Data public static class ColumnOverride {
        private int sourceColumnIndex;
        private boolean included = true;
        /** Explicit user acknowledgement for a mixed-type or entirely empty column. */
        private boolean acknowledged;
        private ExtTableField field;
    }
}

package io.dataease.api.xpack.dataFilling.dto;

import io.dataease.extensions.datafilling.dto.ExtTableField;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/** A non-persisted form proposal. Row numbers are one-based; column indexes are zero-based. */
@Data
public class ExcelParseResponse {
    private String datasourceId;
    private String datasourceType;
    private boolean chineseColumnNamesSupported;
    private String suggestedFormName;
    private String suggestedTableName;
    private List<Sheet> sheets = new ArrayList<>();
    private Integer sheetIndex;
    private int headerRowNumber;
    private boolean scanComplete;
    private int scannedDataRows;
    private boolean valid;
    private List<Column> columns = new ArrayList<>();
    private List<PreviewRow> previewRows = new ArrayList<>();
    private List<Issue> issues = new ArrayList<>();

    @Data public static class Sheet {
        private int index;
        private String name;
    }
    @Data public static class Column {
        private int sourceColumnIndex;
        private String sourceColumnLetter;
        private String originalHeader;
        /** ORIGINAL, CUSTOM, GENERATED_INVALID, GENERATED_UNSUPPORTED, or GENERATED_DUPLICATE. */
        private String columnNameReason;
        private boolean included = true;
        private ExtTableField field;
        private int nonEmptyCount;
        private int emptyCount;
        private int maxCharLength;
        private int maxUtf8Bytes;
        // Max integer digits / max decimal fraction digits observed in this column.
        // Nullable: null means "no numeric cell seen", so the frontend must not read 0 as a real scale
        // (scale 0 is a legitimate decimal scale).
        private Integer integerDigits;
        private Integer scale;
        private List<Issue> issues = new ArrayList<>();
    }
    @Data public static class PreviewRow {
        private int sourceRowNumber;
        private List<String> cells = new ArrayList<>();
    }
    @Data public static class Issue {
        private String code;
        private String message;
        private String severity;
        private Integer rowNumber;
        private Integer columnIndex;
    }
}

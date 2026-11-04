package io.dataease.api.xpack.dataFilling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 批量导入时单条数据错误的明细。
 *
 * <p>用于在「解析阶段」就把所有不合规的数据一次性列出来，避免用户
 * 反复「上传 → 点确定 → 报第一条错 → 改一个 → 再上传」的循环。
 *
 * <p>行号使用 <b>Excel 里的真实行号</b>：表头是第 1 行，第一条数据是第 2 行，
 * 用户可以直接在 Excel 左侧行号上定位。
 */
@Data
@Schema(description = "批量导入单条错误明细")
public class DfImportRowError implements Serializable {

    private static final long serialVersionUID = 9182734650918273645L;

    /** Excel 真实行号（表头=1，首条数据=2） */
    @Schema(description = "Excel 行号（表头为第 1 行）")
    private Integer rowNumber;

    /** 字段显示名，如「数量」 */
    @Schema(description = "出错字段的显示名")
    private String fieldName;

    /** 错误原因，含应有格式与实际值，如「应为整数（BIGINT 范围），实际填的是「abc」」 */
    @Schema(description = "错误原因")
    private String reason;

    public DfImportRowError() {
    }

    public DfImportRowError(Integer rowNumber, String fieldName, String reason) {
        this.rowNumber = rowNumber;
        this.fieldName = fieldName;
        this.reason = reason;
    }

    /** 统一的行号+字段前缀，用于前端拼接展示与错误明细导出。 */
    public String displayPrefix() {
        return "第 " + (rowNumber == null ? "?" : rowNumber) + " 行「"
                + (fieldName == null || fieldName.isBlank() ? "?" : fieldName) + "」：";
    }

    @Override
    public String toString() {
        return displayPrefix() + (reason == null ? "" : reason);
    }
}

package io.dataease.api.sync.task.vo;

import io.dataease.api.sync.task.dto.TableField;
import lombok.Data;

import java.util.List;

/**
 * 字段映射 Excel 导入解析结果
 * 仅做解析和校验，不落库：前端拿到 fields 后应用到表单，保存时仍走 /sync/task/add 或 /update
 *
 * @author fit2cloud
 */
@Data
public class FieldImportVO {

    /**
     * Excel 中有效数据行数（不含表头和空行）
     */
    private int totalRows;

    /**
     * 校验通过的字段映射列表（修改导入模式下为合并后的完整列表）
     */
    private List<TableField> fields;

    /**
     * 校验失败明细
     */
    private List<FieldImportError> errors;

    /**
     * 修改导入模式：新增映射数
     */
    private int addCount;

    /**
     * 修改导入模式：更新映射数
     */
    private int updateCount;

    /**
     * 修改导入模式：因"是否同步=否"被移除的映射数（全新导入为清空的原映射数）
     */
    private int removeCount;

    @Data
    public static class FieldImportError {
        /**
         * Excel 行号（从 2 开始，含表头行）；0 表示全局错误
         */
        private int row;

        /**
         * 相关字段名
         */
        private String field;

        /**
         * 失败原因
         */
        private String reason;
    }
}

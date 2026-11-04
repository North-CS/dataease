package io.dataease.api.xpack.dataFilling.dto;

import io.dataease.extensions.datafilling.dto.ExtTableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class DfExcelData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1162581256875641808L;

    private List<ExtTableField> formFields;

    private List<RowDataDatum> dataList;

    private String id;
    private String excelName;
    private String path;
    private String suffix;

    /**
     * 解析阶段发现的行级错误明细。
     *
     * <p>非空表示本次导入<b>不会写入任何数据</b>（{@link #blocked} 为 true），
     * 用户需按提示修正后重新上传。最多保留 1000 条，超出部分由 totalErrors 反映。
     *
     * <p>为空表示解析通过，可以确认导入。
     */
    private List<DfImportRowError> errors;

    /** 错误总条数（可能大于 errors 的 size，因为 errors 有上限）。 */
    private Integer totalErrors;

    /**
     * 是否禁止确认导入。
     *
     * <p>只要解析阶段发现任意一条错误就为 true，
     * {@code confirmUpload} 会再次校验并拒绝，避免绕过前端直接调接口写入半截数据。
     */
    private Boolean blocked;

}

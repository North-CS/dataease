package io.dataease.api.sync.task.dto;

import lombok.Data;

import java.util.List;

/**
 * 字段映射 Excel 导入导出请求参数
 * 导出时：sourceFieldList + targetFieldList 用于生成模板（已映射字段带出当前配置，未映射字段默认"不同步"）
 * 导入时：mode 决定导入行为，sourceFieldList/targetFieldList/targetTypeList 用于校验
 *
 * @author fit2cloud
 */
@Data
public class FieldMappingExcelRequest {

    /**
     * 导入模式 update-修改导入 replace-全新导入
     */
    private String mode;

    /**
     * 源表字段列表
     */
    private List<TableField> sourceFieldList;

    /**
     * 当前目标字段映射列表
     */
    private List<TableField> targetFieldList;

    /**
     * 目标数据库支持的数据类型列表
     */
    private List<String> targetTypeList;
}

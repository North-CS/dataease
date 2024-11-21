
INSERT INTO `core_sys_startup_job` VALUES ('chartFilterDynamic', 'chartFilterDynamic', 'ready');


alter table `core_dataset_table_field` add params text null comment '计算字段参数';

alter table `core_datasource`
    add `enable_data_fill` tinyint default 0 null comment '启用数据填报功能';


ALTER TABLE `visualization_outer_params_target_view_info`
    MODIFY COLUMN `target_view_id` varchar(50) NULL DEFAULT NULL COMMENT '联动视图ID/联动过滤项ID';
ALTER TABLE `visualization_outer_params_target_view_info`
    ADD COLUMN `target_ds_id` varchar(50) NULL COMMENT '联动数据集id/联动过滤组件id' ;


alter table `core_chart_view`
    add flow_map_start_name longtext comment '流向地图起点名称field';
alter table `core_chart_view`
    add flow_map_end_name longtext comment '流向地图终点名称field';
alter table `core_chart_view`
    add ext_color longtext comment '颜色维度field';

update visualization_outer_params_target_view_info set target_ds_id = (select ccv.table_id from visualization_outer_params_target_view_info tvi INNER JOIN core_chart_view ccv on tvi.target_view_id = ccv.id)
where target_view_id = (select ccv.id from visualization_outer_params_target_view_info tvi INNER JOIN core_chart_view ccv on tvi.target_view_id = ccv.id);

DROP TABLE IF EXISTS `core_font`;
CREATE TABLE `core_font`
(
    `id`                bigint       NOT NULL COMMENT 'ID',
    `name`              varchar(255) NOT NULL COMMENT '字体名称',
    `file_name`         varchar(255) default NULL COMMENT '文件名称',
    `file_trans_name`   varchar(255) default NULL COMMENT '文件转换名称',
    `is_default`         tinyint(1)       default 0 COMMENT '是否默认',
    `update_time`        bigint     NOT NULL COMMENT '更新时间',
    `is_BuiltIn`         tinyint(1)       default 0 COMMENT '是否内置',
    PRIMARY KEY (`id`)
);
ALTER TABLE  `core_font` ADD COLUMN `size` DOUBLE NULL AFTER `is_BuiltIn`;
ALTER TABLE  `core_font` ADD COLUMN `size_type` varchar(255) NULL AFTER `size`;
INSERT INTO  `core_font` (`id`, `name`, `is_default`, `update_time`, `is_BuiltIn`) VALUES ('1', 'PingFang', '1', '0', '1');

INSERT INTO `core_sys_setting`(`id`, `pkey`, `pval`, `type`, `sort`)
VALUES (1048232869488627719, 'basic.defaultSort', '1', 'text', 13);

INSERT INTO `core_sys_setting`(`id`, `pkey`, `pval`, `type`, `sort`)
VALUES (1048232869488627720, 'basic.defaultOpen', '0', 'text', 14);

INSERT INTO `core_menu` VALUES (70, 0, 1, 'msg', NULL, 200, NULL, '/msg', 1, 1, 0);

UPDATE `xpack_setting_authentication` set `synced` = 0 where `name` = 'oidc' or name = 'cas';

ALTER TABLE `data_visualization_info`
    ADD COLUMN `content_id` varchar(50) NULL DEFAULT '0' COMMENT '内容标识';

ALTER TABLE `visualization_link_jump_target_view_info`
    ADD COLUMN `target_type` varchar(50) NULL DEFAULT 'view' COMMENT '联动目标类型 view 图表 filter 过滤组件 outParams 外部参数';

ALTER TABLE `visualization_link_jump_target_view_info`
    MODIFY COLUMN `target_view_id` varchar(50) NULL DEFAULT NULL COMMENT '目标图表ID' AFTER `source_field_active_id`,
    MODIFY COLUMN `target_field_id` varchar(50) NULL DEFAULT NULL COMMENT '目标字段ID' AFTER `target_view_id`;

update visualization_link_jump_target_view_info set target_type = 'view';
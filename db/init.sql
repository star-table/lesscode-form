CREATE TABLE `lc_app` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用名称',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '应用类型, 1 报表, 2 仪表盘',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图标',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态, 1可用,2禁用',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` bigint NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

CREATE TABLE `lc_app_pkg` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用包名称',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图标',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` bigint NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用包';

CREATE TABLE `lc_app_group` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用组名称',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` bigint NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用组';

CREATE TABLE `lc_app_relation` (
  `id` bigint NOT NULL COMMENT '主键',
  `app_id` bigint NOT NULL DEFAULT '0' COMMENT '应用id',
  `relation_type` bigint NOT NULL DEFAULT '0' COMMENT '关联类型',
  `relation_id` bigint NOT NULL DEFAULT '0' COMMENT '关联id',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` bigint NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用关联表';

CREATE TABLE `lc_app_field` (
  `id` bigint NOT NULL COMMENT '主键',
  `app_id` bigint NOT NULL DEFAULT '0' COMMENT '应用id',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '0:默认,1:单行文本 2:手机 3:电话 4: 邮箱 5:下拉框 6: 日期 7:省市区 8: 数字 9:链接，10：单选，11：多选，12：附件上传，13：起止时间，14：图片选择，15：多行文本，16：描述，17：定位，18：表格，19：人员，20：部门，21：部门和人员，22:数据关联,23:ocr',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表的字段名',
  `key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表的字段标识key',
  `comment` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字段描述',
  `config` json NOT NULL COMMENT '字段配置',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用-表属性';

CREATE TABLE `lc_app_value` (
  `id` bigint NOT NULL COMMENT '主键',
  `app_id` bigint NOT NULL DEFAULT '0' COMMENT '应用id',
  `content` json NOT NULL COMMENT '内容json',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用-表属性';


CREATE TABLE `lc_app_field_template` (
  `id` bigint NOT NULL COMMENT '主键',
  `app_id` bigint NOT NULL DEFAULT '0' COMMENT '应用id',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '0:默认,1:单行文本 2:手机 3:电话 4: 邮箱 5:下拉框 6: 日期 7:省市区 8: 数字 9:链接，10：单选，11：多选，12：附件上传，13：起止时间，14：图片选择，15：多行文本，16：描述，17：定位，18：表格，19：人员，20：部门，21：部门和人员，22:数据关联,23:ocr',
  `config` json NOT NULL COMMENT '字段配置',
  `creator` bigint NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updator` bigint NOT NULL DEFAULT '0' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁',
  `del_flag` tinyint NOT NULL DEFAULT '2' COMMENT '是否删除,1是,2否',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用-表模板';





-- 期刊基本信息表
CREATE TABLE `journal_basic` (
  `id` bigint NOT NULL,
  `journal_name` varchar(255) DEFAULT NULL COMMENT '期刊名称',
  `domain_url` varchar(255) DEFAULT NULL COMMENT '期刊域名',
  `current_issue_url` varchar(255) DEFAULT NULL COMMENT '当前期URL',
  `history_issue_list_url` varchar(255) DEFAULT NULL COMMENT '历史期列表URL',
  `is_contain_current` tinyint(1) DEFAULT NULL COMMENT '是否包含当前期',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊基本信息表';

-- 期刊卷期信息表
CREATE TABLE `journal_issue` (
  `id` bigint NOT NULL,
  `basic_id` bigint NOT NULL COMMENT '期刊基本信息ID',
  `issue_url` varchar(255) DEFAULT NULL COMMENT '期刊卷期URL',
  `year` int DEFAULT NULL COMMENT '年份',
  `period` varchar(50) DEFAULT NULL COMMENT '期号',
  `grab_status` varchar(20) DEFAULT NULL COMMENT '抓取状态',
  `retry_count` int DEFAULT 0 COMMENT '重试次数',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊卷期信息表';

-- 期刊论文基本信息表
CREATE TABLE `journal_paper` (
  `id` bigint NOT NULL,
  `basic_id` bigint NOT NULL COMMENT '期刊基本信息ID',
  `issue_id` bigint NOT NULL COMMENT '期刊卷期ID',
  `year` int DEFAULT NULL COMMENT '年份',
  `period` varchar(50) DEFAULT NULL COMMENT '期号',
  `title` varchar(500) DEFAULT NULL COMMENT '论文标题',
  `order_num` int DEFAULT NULL COMMENT '排序号',
  `paper_url` varchar(255) DEFAULT NULL COMMENT '论文URL',
  `grab_status` varchar(20) DEFAULT NULL COMMENT '抓取状态',
  `retry_count` int DEFAULT 0 COMMENT '重试次数',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊论文信息表';

-- 期刊论文详情信息表
CREATE TABLE `journal_paper_detail` (
  `id` bigint NOT NULL,
  `paper_id` bigint NOT NULL COMMENT '论文ID',
  `html_store_path` varchar(255) DEFAULT NULL COMMENT 'HTML存储路径',
  `title` varchar(500) DEFAULT NULL COMMENT '标题',
  `sub_title` varchar(500) DEFAULT NULL COMMENT '副标题',
  `summary` text DEFAULT NULL COMMENT '摘要',
  `keywords` varchar(500) DEFAULT NULL COMMENT '关键词',
  `fund` varchar(500) DEFAULT NULL COMMENT '基金',
  `page_count` int DEFAULT NULL COMMENT '页数',
  `article_page_number` varchar(50) DEFAULT NULL COMMENT '文章页码',
  `pdf_page_number` varchar(50) DEFAULT NULL COMMENT 'PDF页码',
  `order_field` varchar(50) DEFAULT NULL COMMENT '排序字段',
  `author_names` text DEFAULT NULL COMMENT '作者姓名列表',
  `affiliations_name` text DEFAULT NULL COMMENT '作者单位列表',
  `doi` varchar(100) DEFAULT NULL COMMENT 'DOI标识',
  `clcs` varchar(100) DEFAULT NULL COMMENT '中图分类号',
  `pdf_url` varchar(255) DEFAULT NULL COMMENT 'PDF链接',
  `pdf_store_path` varchar(255) DEFAULT NULL COMMENT 'PDF存储路径',
  `pdf_grab_status` varchar(20) DEFAULT NULL COMMENT 'PDF抓取状态',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊论文详情信息表';

-- 期刊论文机构信息对照表
CREATE TABLE `journal_paper_agency` (
  `id` bigint NOT NULL,
  `paper_id` bigint NOT NULL COMMENT '论文ID',
  `affiliations_code` varchar(100) DEFAULT NULL COMMENT '机构代码',
  `affiliations_name` varchar(500) DEFAULT NULL COMMENT '机构名称',
  `sort` int DEFAULT NULL COMMENT '排序',
  `province_and_city` varchar(100) DEFAULT NULL COMMENT '省市',
  `zip_code` varchar(20) DEFAULT NULL COMMENT '邮编',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊论文机构对照表';

-- 期刊论文作者信息对照表
CREATE TABLE `journal_paper_author` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `paper_id` BIGINT NOT NULL COMMENT '论文ID',
    `author_name` VARCHAR(255) COMMENT '作者姓名',
    `affiliations_codes` VARCHAR(255) COMMENT '机构代码',
    `sort` INT COMMENT '排序',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊论文作者对照表';

-- 期刊卷期解析规则表（解析卷期下的论文链接）
CREATE TABLE `journal_issue_rule` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `basic_id` BIGINT NOT NULL COMMENT '期刊基本信息ID',
    `issue_url_prefix` VARCHAR(500) NOT NULL COMMENT '卷期url前缀，识别同一本期刊下多套卷期规则',
    `static_page` tinyint(1) DEFAULT NULL COMMENT '是否静态页面',
    `x_path_rule` VARCHAR(1000) COMMENT 'xPath解析规则',
    `class_style_rule` VARCHAR(1000) COMMENT '类样式解析规则',
    `script` TEXT COMMENT '脚本支持方式',
    `created` DATETIME COMMENT '创建时间',
    `updated` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊卷期解析规则表';

-- 期刊论文详情数据解析规则表
CREATE TABLE `journal_paper_detail_rule` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `basic_id` BIGINT NOT NULL COMMENT '期刊基本信息ID',
    `paper_url_prefix` VARCHAR(500) NOT NULL COMMENT '论文URL前缀',
    `paper_url` VARCHAR(500) COMMENT '论文页链接',
    `static_page` tinyint(1) DEFAULT NULL COMMENT '是否静态页面',
    `title_rule` VARCHAR(1000) COMMENT '论文标题规则',
    `abstract_rule` VARCHAR(1000) COMMENT '论文摘要规则',
    `keywords_rule` VARCHAR(1000) COMMENT '论文关键词规则',
    `fund_rule` VARCHAR(1000) COMMENT '基金规则',
    `author_rule` VARCHAR(1000) COMMENT '作者规则',
    `dept_rule` VARCHAR(1000) COMMENT '机构规则',
    `doi_rule` VARCHAR(500) COMMENT 'DOI规则',
    `clcs_rule` VARCHAR(500) COMMENT '分类规则',
    `pdf_rule` VARCHAR(500) COMMENT 'PDF规则',
    `issue_rule` VARCHAR(500) COMMENT '卷期规则',
    `created` DATETIME COMMENT '创建时间',
    `updated` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊论文详情数据解析规则表';

-- 期刊数据抓取执行计划表
CREATE TABLE `journal_execute_plan` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `basic_id` BIGINT NOT NULL COMMENT '期刊基本信息ID',
    `issue_id` BIGINT NOT NULL COMMENT '期刊卷期ID',
    `issue_url` VARCHAR(500) COMMENT '期刊卷期URL',
    `exec_status` VARCHAR(50) COMMENT '执行状态',
    `created` DATETIME COMMENT '创建时间',
    `updated` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊数据抓取执行计划表';

-- 期刊数据抓取执行历史记录表
CREATE TABLE `journal_execute_history` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `basic_id` BIGINT NOT NULL COMMENT '期刊基本信息ID',
    `issue_id` BIGINT NOT NULL COMMENT '期刊卷期ID',
    `plan_id` BIGINT NOT NULL COMMENT '执行计划ID',
    `issue_url` VARCHAR(500) COMMENT '期刊期号URL',
    `phase` VARCHAR(50) COMMENT '执行阶段',
    `status` VARCHAR(50) COMMENT '执行状态',
    `success_num` INT COMMENT '成功数量',
    `failed_num` INT COMMENT '失败数量',
    `created` DATETIME COMMENT '创建时间',
    `updated` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='期刊数据抓取执行历史记录表';
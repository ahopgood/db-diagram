CREATE TABLE `attributes` (
  `attribute_id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `attribute_type_id` varchar(255) DEFAULT NULL,
  `label_partner` varchar(255) DEFAULT NULL,
  `label_customer` varchar(255) DEFAULT NULL,
  `is_legacy` tinyint(1) DEFAULT NULL,
  `max_selectable_attribute_values` int(11) DEFAULT NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  `is_mandatory` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`attribute_id`),
  UNIQUE KEY `attribute_id` (`attribute_id`),
  KEY `attribute_type_id` (`attribute_type_id`),
  CONSTRAINT `attributes_ibfk_1` FOREIGN KEY (`attribute_type_id`) REFERENCES `attribute_types` (`attribute_type_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
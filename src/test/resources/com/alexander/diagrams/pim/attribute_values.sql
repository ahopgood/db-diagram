CREATE TABLE `attribute_values` (
  `attribute_value_id` varchar(36) NOT NULL,
  `attribute_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  `is_default` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`attribute_value_id`),
  UNIQUE KEY `attribute_value_id` (`attribute_value_id`),
  KEY `attribute_id` (`attribute_id`),
  CONSTRAINT `attribute_values_ibfk_1` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`attribute_id`) ON DELETE SET NULL,
--  orphan foreign key
  CONSTRAINT `attribute_values_ibfk_2` FOREIGN KEY (`name`) REFERENCES `test` (`test_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC

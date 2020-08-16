 CREATE TABLE `inventory_attribute_values` (
  `inventory_id` varchar(36) NOT NULL,
  `attribute_value_id` varchar(36) NOT NULL,
  PRIMARY KEY (`inventory_id`,`attribute_value_id`),
  UNIQUE KEY `unique_constraint` (`inventory_id`,`attribute_value_id`),
  KEY `attribute_value_id` (`attribute_value_id`),
  CONSTRAINT `inventory_attribute_values_ibfk_1` FOREIGN KEY (`attribute_value_id`) REFERENCES `attribute_values` (`attribute_value_id`) ON DELETE CASCADE,
  CONSTRAINT `inventory_attribute_values_ibfk_2` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`inventory_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
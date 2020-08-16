CREATE TABLE `product_types` (
  `product_type_id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_product_type_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`product_type_id`),
  UNIQUE KEY `product_type_id` (`product_type_id`),
  KEY `parent_product_type_id` (`parent_product_type_id`),
  CONSTRAINT `product_types_ibfk_1` FOREIGN KEY (`parent_product_type_id`) REFERENCES `product_types` (`product_type_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
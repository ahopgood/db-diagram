CREATE TABLE `pim_product_type_attributes` (
  `product_type_id` varchar(36) NOT NULL,
  `attribute_id` varchar(36) NOT NULL,
  PRIMARY KEY (`product_type_id`,`attribute_id`),
  KEY `attribute_id` (`attribute_id`),
  CONSTRAINT `pim_product_type_attributes_ibfk_1` FOREIGN KEY (`product_type_id`) REFERENCES `product_types` (`product_type_id`) ON DELETE CASCADE,
  CONSTRAINT `pim_product_type_attributes_ibfk_2` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`attribute_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
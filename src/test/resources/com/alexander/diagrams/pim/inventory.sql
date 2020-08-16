CREATE TABLE `inventory` (
  `inventory_id` varchar(36) NOT NULL,
  `product_code` varchar(36) NOT NULL,
  `product_type_id` varchar(36) DEFAULT NULL,
  `last_modified_by_user` varchar(36) DEFAULT NULL,
  `last_modified_by_partner` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `inventory_id` (`inventory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
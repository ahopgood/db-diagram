Create Table: CREATE TABLE `product_option_values` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_option_id` int(11) DEFAULT NULL,
  `attribute_value_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `price_modifier` varchar(255) DEFAULT NULL,
  `price_modifier_value` decimal(14,6) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL,
  `lock_version` int(11) DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `created_by_id` int(11) DEFAULT NULL,
  `updated_by_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_option_values_ibfk_created_by_id` (`created_by_id`),
  KEY `product_option_values_ibfk_updated_by_id` (`updated_by_id`),
  KEY `index_product_option_values_on_product_option_id_and_deleted_at` (`product_option_id`,`deleted_at`),
  KEY `index_product_option_values_on_avid_poid_deleted_at` (`product_option_id`,`attribute_value_id`,`deleted_at`),
  CONSTRAINT `product_option_values_ibfk_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `product_option_values_ibfk_product_option_id` FOREIGN KEY (`product_option_id`) REFERENCES `product_options` (`id`),
  CONSTRAINT `product_option_values_ibfk_updated_by_id` FOREIGN KEY (`updated_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3891625 DEFAULT CHARSET=utf8
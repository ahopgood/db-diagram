Create Table: CREATE TABLE `product_attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `type_attribute_id` int(11) DEFAULT NULL,
  `attribute_value_id` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_product_type_attr_attr_value_ids` (`product_id`,`type_attribute_id`,`attribute_value_id`),
  KEY `index_product_attributes_on_attribute_value_id` (`attribute_value_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3157718 DEFAULT CHARSET=utf8
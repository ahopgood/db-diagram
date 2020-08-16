Create Table: CREATE TABLE `attribute_values_products` (
  `product_id` int(11) NOT NULL,
  `attribute_value_id` int(11) NOT NULL,
  KEY `index_attribute_values_products_pid_avid` (`product_id`,`attribute_value_id`),
  KEY `index_attribute_values_products_avid_pid` (`attribute_value_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
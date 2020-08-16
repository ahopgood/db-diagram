Create Table: CREATE TABLE `type_attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_type_id` int(11) NOT NULL,
  `customer_facing` tinyint(1) NOT NULL DEFAULT '0',
  `required` tinyint(1) NOT NULL DEFAULT '0',
  `allows_multiselect` tinyint(1) NOT NULL DEFAULT '0',
  `admin_only` tinyint(1) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL,
  `lock_version` int(11) DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `label` varchar(255) NOT NULL DEFAULT '',
  `default_attribute_name_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `by_default_id_and_product_type_id` (`default_attribute_name_id`,`product_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6663 DEFAULT CHARSET=utf8
CREATE TABLE `attribute_values` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `default_attribute_name_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `classification` varchar(255) NOT NULL DEFAULT '',
  `position` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `unique_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_attribute_values_name_danid_classification` (`name`,`default_attribute_name_id`,`classification`),
  UNIQUE KEY `index_attribute_values_on_unique_key` (`unique_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1583 DEFAULT CHARSET=utf8
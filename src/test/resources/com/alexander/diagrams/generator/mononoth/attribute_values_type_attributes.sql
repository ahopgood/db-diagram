Create Table: CREATE TABLE `attribute_values_type_attributes` (
  `type_attribute_id` int(11) DEFAULT NULL,
  `attribute_value_id` int(11) DEFAULT NULL,
  UNIQUE KEY `unique_idx` (`type_attribute_id`,`attribute_value_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
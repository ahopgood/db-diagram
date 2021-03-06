CREATE TABLE `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sku` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `handle` varchar(255) DEFAULT NULL,
  `availability` varchar(255) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `published_at` datetime DEFAULT NULL,
  `discontinued_at` datetime DEFAULT NULL,
  `partner_id` int(11) DEFAULT NULL,
  `image_id` int(11) DEFAULT NULL,
  `sale_type` varchar(255) NOT NULL,
  `gross_price` decimal(14,6) DEFAULT NULL,
  `sale_value` decimal(14,6) DEFAULT NULL,
  `specific_commission_rate` decimal(4,2) DEFAULT NULL,
  `back_in_stock_at` date DEFAULT NULL,
  `excerpt` varchar(255) DEFAULT NULL,
  `allows_gift_wrap` tinyint(1) DEFAULT NULL,
  `allows_gift_message` tinyint(1) NOT NULL DEFAULT '0',
  `delivery_class_id` int(11) DEFAULT NULL,
  `delivery_size_id` int(11) DEFAULT NULL,
  `delivery_time_id` int(11) DEFAULT NULL,
  `delivery_explanation` varchar(255) DEFAULT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  `volume` decimal(6,2) DEFAULT NULL,
  `optimised` tinyint(1) NOT NULL DEFAULT '0',
  `exclusive` tinyint(1) NOT NULL DEFAULT '0',
  `free_gift_wrap` tinyint(1) NOT NULL DEFAULT '0',
  `boosted` tinyint(1) NOT NULL DEFAULT '0',
  `eco_friendly` tinyint(1) NOT NULL DEFAULT '0',
  `made_in_britain` tinyint(1) NOT NULL DEFAULT '0',
  `worker` varchar(255) DEFAULT NULL,
  `delta` tinyint(1) NOT NULL DEFAULT '0',
  `inventory_status` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `first_sold_at` datetime DEFAULT NULL,
  `last_sold_at` datetime DEFAULT NULL,
  `sold_quantity` int(11) DEFAULT NULL,
  `lock_version` int(11) DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by_id` int(11) DEFAULT NULL,
  `created_by_id` int(11) DEFAULT NULL,
  `use_default_delivery_time` tinyint(1) NOT NULL DEFAULT '0',
  `use_default_delivery_zones` tinyint(1) NOT NULL DEFAULT '0',
  `use_default_delivery_services` tinyint(1) NOT NULL DEFAULT '0',
  `force_free_delivery` tinyint(1) NOT NULL DEFAULT '0',
  `monitored` tinyint(1) NOT NULL DEFAULT '0',
  `xmas_last_order_date` date DEFAULT NULL,
  `use_default_xmas_last_order_date` tinyint(1) NOT NULL DEFAULT '0',
  `gross_price_on_sale` decimal(14,6) NOT NULL,
  `title_locked` tinyint(1) NOT NULL DEFAULT '0',
  `gross_price_locked` tinyint(1) NOT NULL DEFAULT '0',
  `master_commission_rate` decimal(4,2) DEFAULT NULL,
  `confirmed_product_type_id` int(11) DEFAULT NULL,
  `proposed_product_type_id` int(11) DEFAULT NULL,
  `lead_time` int(11) DEFAULT NULL,
  `product_reviews_count` int(11) NOT NULL DEFAULT '0',
  `positive_product_reviews_count` int(11) NOT NULL DEFAULT '0',
  `product_ratings_count` int(11) NOT NULL DEFAULT '0',
  `product_ratings_total` int(11) NOT NULL DEFAULT '0',
  `personalisable` tinyint(1) NOT NULL DEFAULT '0',
  `xmas_last_order_date_express` date DEFAULT NULL,
  `has_express_delivery` tinyint(1) NOT NULL DEFAULT '0',
  `storefront_favourite` tinyint(1) NOT NULL DEFAULT '0',
  `has_international_delivery` tinyint(1) NOT NULL DEFAULT '0',
  `storefront_ordering` int(11) NOT NULL DEFAULT '1',
  `flagged_for_xmas` tinyint(1) NOT NULL DEFAULT '0',
  `free_delivery` tinyint(1) NOT NULL DEFAULT '0',
  `bespoke` tinyint(1) NOT NULL DEFAULT '0',
  `flagged_for_merchandising` tinyint(1) NOT NULL DEFAULT '0',
  `involvement` varchar(255) NOT NULL DEFAULT '',
  `has_default_printable_image` int(1) NOT NULL DEFAULT '0',
  `skip_title_validation` tinyint(1) NOT NULL DEFAULT '0',
  `vat_rate_value` decimal(4,2) DEFAULT NULL,
  `vat_rate_handle` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `products_ibfk_partner_id` (`partner_id`),
  KEY `products_ibfk_image_id` (`image_id`),
  KEY `products_ibfk_delivery_class_id` (`delivery_class_id`),
  KEY `products_ibfk_delivery_size_id` (`delivery_size_id`),
  KEY `products_ibfk_delivery_time_id` (`delivery_time_id`),
  KEY `products_ibfk_updated_by_id` (`updated_by_id`),
  KEY `products_ibfk_created_by_id` (`created_by_id`),
  KEY `index_products_on_handle` (`handle`),
  KEY `index_products_on_availability` (`availability`),
  KEY `index_products_on_priority` (`priority`),
  KEY `index_products_on_published_at` (`published_at`),
  KEY `index_products_on_sold_quantity` (`sold_quantity`),
  KEY `index_products_on_inventory_status` (`inventory_status`),
  KEY `index_products_on_rank` (`rank`),
  KEY `index_products_on_created_at` (`created_at`),
  KEY `index_products_on_xmas_last_order_date` (`xmas_last_order_date`),
  KEY `index_products_on_use_default_xmas_last_order_date` (`use_default_xmas_last_order_date`),
  KEY `products_ibfk_confirmed_product_type_id` (`confirmed_product_type_id`),
  KEY `products_ibfk_proposed_product_type_id` (`proposed_product_type_id`),
  KEY `index_products_on_personalisable` (`personalisable`),
  CONSTRAINT `products_ibfk_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `products_ibfk_delivery_class_id` FOREIGN KEY (`delivery_class_id`) REFERENCES `delivery_classes` (`id`),
  CONSTRAINT `products_ibfk_delivery_size_id` FOREIGN KEY (`delivery_size_id`) REFERENCES `delivery_sizes` (`id`),
  CONSTRAINT `products_ibfk_delivery_time_id` FOREIGN KEY (`delivery_time_id`) REFERENCES `delivery_times` (`id`),
  CONSTRAINT `products_ibfk_image_id` FOREIGN KEY (`image_id`) REFERENCES `product_images` (`id`),
  CONSTRAINT `products_ibfk_partner_id` FOREIGN KEY (`partner_id`) REFERENCES `partners` (`id`),
  CONSTRAINT `products_ibfk_proposed_product_type_id` FOREIGN KEY (`proposed_product_type_id`) REFERENCES `product_types` (`id`),
  CONSTRAINT `products_ibfk_updated_by_id` FOREIGN KEY (`updated_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=787403 DEFAULT CHARSET=utf8
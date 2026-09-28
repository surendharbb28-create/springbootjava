CREATE DATABASE IF NOT EXISTS farm_inventory
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE farm_inventory;

CREATE TABLE IF NOT EXISTS crops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_crops_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS harvest_batches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    crop_id BIGINT NOT NULL,
    quantity DECIMAL(12, 2) NOT NULL,
    harvest_date DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_harvest_batches_crop
        FOREIGN KEY (crop_id) REFERENCES crops (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sales (
    id BIGINT NOT NULL AUTO_INCREMENT,
    crop_id BIGINT NOT NULL,
    quantity DECIMAL(12, 2) NOT NULL,
    price_per_unit DECIMAL(12, 2) NOT NULL,
    sale_date DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sales_crop
        FOREIGN KEY (crop_id) REFERENCES crops (id)
) ENGINE=InnoDB;
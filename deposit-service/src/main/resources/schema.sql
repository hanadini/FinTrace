CREATE TABLE IF NOT EXISTS deposit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount NUMERIC(38, 2) NOT NULL,
    customer_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    currency ENUM('EUR', 'USD')
);
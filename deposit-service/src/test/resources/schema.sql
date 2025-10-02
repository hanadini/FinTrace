CREATE TABLE IF NOT EXISTS deposit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    customer_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    currency ENUM('EUR', 'USD')
);
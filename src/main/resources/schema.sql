CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    type VARCHAR(10) -- 'REAL' or 'LEGAL'
);

CREATE TABLE real_customer (
    id BIGINT PRIMARY KEY,
    family VARCHAR(255),
    birth_date DATE,
    FOREIGN KEY (id) REFERENCES customer(id) ON DELETE CASCADE
);

CREATE TABLE legal_customer (
    id BIGINT PRIMARY KEY,
    fax VARCHAR(20),
    registration_number VARCHAR(50),
    FOREIGN KEY (id) REFERENCES customer(id) ON DELETE CASCADE
);
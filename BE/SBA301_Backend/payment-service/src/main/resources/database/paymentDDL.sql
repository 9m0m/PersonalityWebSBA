-- ENUMS
--CREATE TYPE transaction_status AS ENUM ('PENDING', 'PAID', 'CANCEL');
--CREATE TYPE payment_method AS ENUM ('PAYOS', 'EWALLET');

-- TRANSACTION TABLE
CREATE TABLE transaction (
                             transaction_id bigint PRIMARY KEY, --create manual
                             order_id bigint NOT NULL,
                             amount bigint NOT NULL,
                             time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             description VARCHAR(255) NOT NULL,
                             checkout_url VARCHAR(255) NOT NULL,
                             qr_code VARCHAR(255) NOT NULL,
                             uid VARCHAR(255),
                             status VARCHAR(20) NOT NULL, --('PENDING', 'PAID', 'CANCEL')
                             method VARCHAR(20) NOT NULL --('PAYOS', 'EWALLET')
);

-- PAYMENT TABLE
CREATE TABLE payment (
                         payment_id VARCHAR(50) PRIMARY KEY,
                         amount INT NOT NULL,
                         fee INT NOT NULL,
                         transaction_id INT NOT NULL,
                         -- uid VARCHAR(255),
                         CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES transaction(transaction_id)
);

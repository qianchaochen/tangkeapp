-- V3__Wallet_payment_tables_and_optimistic_lock.sql
-- Wallet payment core: optimistic locking + recharge order tracking + idempotency support

-- Optimistic locking for accounts
ALTER TABLE accounts
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0 AFTER total_spend;

-- Idempotency support for transactions (e.g., recharge order_no)
ALTER TABLE transactions
    ADD COLUMN reference_no VARCHAR(64) NULL AFTER project_type;

CREATE UNIQUE INDEX uk_transactions_type_reference_no
    ON transactions (type, reference_no);

-- Recharge orders: persisted pending unified orders and payment callbacks
CREATE TABLE recharge_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    bonus_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    promotion_id BIGINT NULL,
    status VARCHAR(20) NOT NULL,
    provider_prepay_id VARCHAR(128),
    provider_transaction_id VARCHAR(128),
    request_metadata TEXT,
    callback_metadata TEXT,
    paid_at DATETIME,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_recharge_orders_order_no (order_no),
    UNIQUE KEY uk_recharge_orders_provider_tx (channel, provider_transaction_id),

    INDEX idx_recharge_orders_customer (customer_id),
    INDEX idx_recharge_orders_account (account_id),
    INDEX idx_recharge_orders_status (status),

    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recharge order state transition audit log
CREATE TABLE recharge_order_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recharge_order_id BIGINT NOT NULL,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    message VARCHAR(500),
    raw_payload TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_recharge_order_events_order (recharge_order_id),
    INDEX idx_recharge_order_events_created (created_at),

    FOREIGN KEY (recharge_order_id) REFERENCES recharge_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

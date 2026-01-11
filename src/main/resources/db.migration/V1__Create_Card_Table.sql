CREATE TABLE cards (
                       card_number VARCHAR(19) NOT NULL PRIMARY KEY,
                       cardholder_name VARCHAR(255) NOT NULL,
                       cvv VARCHAR(4) NOT NULL,
                       expiry_date VARCHAR(5) NOT NULL,
                       card_type VARCHAR(20) NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_cardholder_name (cardholder_name),
                       INDEX idx_card_type (card_type),
                       INDEX idx_status (status)
);

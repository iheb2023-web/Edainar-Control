ALTER TABLE cards
    ADD COLUMN card_number VARCHAR(14),
    ADD COLUMN secret_code VARCHAR(20),
    ADD COLUMN phone_number VARCHAR(30),
    ADD COLUMN expiration_date DATE,
    ADD COLUMN owner_name VARCHAR(100);

CREATE INDEX idx_cards_card_number ON cards(card_number);